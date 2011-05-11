package uk.ac.sanger.artemis.components.variant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import net.sf.samtools.util.SeekableStream;

/**
 * Written independently to, but bugfixed by looking at the Savant SeekableFTPStream. 
 * @author gv1
 *
 */
public class FTPSeekableStream extends SeekableStream {

	private static final Logger logger = Logger.getLogger(FTPSeekableStream.class);

	private static final String defaultUser = "anonymous";
	private static final String defaultPassword = "";

	private URL url;
	private String host;
	
	private String user;
	private String password;
	
	private String remoteFilePath;
	private String remoteFileName;
	
	private FTPClient _client;

	private long position = 0;
	private long length = -1;
	
	private File tmpFolder;
	private File index;

	public FTPSeekableStream(URL url)
			throws SocketException, IOException {
		this(url, defaultUser, defaultPassword);
	}

	public FTPSeekableStream(URL url, String user, String password) throws SocketException, IOException {
		
		this.url = url;
		this.user = user;
		this.password = password;
		
		host = url.getHost();
		remoteFilePath = url.getPath();
		
		String[] split = remoteFilePath.split("/");
		if (split.length > 0) {
			remoteFileName = split[split.length - 1 ];
		} else {
			remoteFileName = remoteFilePath;
		}
		
		logger.info(String.format("Setup a stream for %s %s %s %s", host, remoteFilePath, this.user, this.password));

	}
	
	private FTPClient getClient() throws SocketException, IOException {
		
		if (_client == null) {
			
			FTPClient client = new FTPClient();
			
			client.connect(host);
			client.login(this.user, this.password);
			
			logger.debug(client.getReplyString());
			
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			client.setSoTimeout(10000);
	        
	        int reply = client.getReplyCode();
			logger.info(reply);

			if (!FTPReply.isPositiveCompletion(reply)) {
				close();
				throw new IOException("FTP server refused connection.");
			}
			
			_client = client;
		}
		return _client;
	}
	
	@Override
	public long length() {
		logger.info("length " + length);
		
		if (length != -1) {
			return length;
		}
		
		try {
			for (FTPFile f : getClient().listFiles(remoteFilePath)) {
				if (f.getName().equals(remoteFilePath)) {
					length = f.getSize();					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return length;
		
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		
		FTPClient client = getClient();
		
		if (position != 0) {
			client.setRestartOffset(position);
		}
		
		InputStream in = client.retrieveFileStream(remoteFilePath);
		
		logger.info("read " + offset + "-" + (offset + length));
		logger.info(in);
		
		if (in == null) {
			throw new IOException("Could not get stream");
		}
		
		int i = 0;
		
		while (i < length) {
			int bytesRead = in.read(bytes, offset + i , length -i);
			if (bytesRead < 0) {
				if (i == 0) {
					return -1;
				} else {
					break;
				}
			}
			logger.info(i);
			i += bytesRead;
		}
		

		in.close();
		client.completePendingCommand();
		
		position += i;
		
		return i;
	}
	
	 

	@Override
	public void close() throws IOException {
		if (_client != null) {
			_client.disconnect();
		}
	}

	@Override
	public boolean eof() throws IOException {
		if (position >= length()) {
			return true;
		}
		return false;
	}

	@Override
	public String getSource() {
		return url.toString();
	}

	@Override
	public void seek(long position) throws IOException {
		logger.info("seek " + position);
		this.position = position;

	}
	@Override
	public int read() throws IOException {
		logger.info("read");
		
		FTPClient client = getClient();
		
		client.setRestartOffset(position);
		InputStream in = client.retrieveFileStream(remoteFilePath);
		int read = in.read();
		
		position++;
		
		in.close();
		client.completePendingCommand();
		
		return read;
		
	}
	

	public File getTmpFolder() {
		if (tmpFolder == null) {
			tmpFolder = new File("/tmp/");
		}
		return tmpFolder;
	}
	
	public void setTmpFolder(File tmpFolder) throws IOException {
		if (! tmpFolder.isDirectory()) {
			throw new IOException("File " + tmpFolder.getName() + " is not a folder");
		}
		this.tmpFolder = tmpFolder;
	}
	
	public File getIndexFile() throws IOException {
		
		if (index == null) {
			
			FTPClient client = getClient();
			
			String indexFileName = remoteFileName + ".bai";
			String localPath = getTmpFolder().getAbsolutePath()  + "/" + indexFileName;
			String remotePath = remoteFilePath + ".bai";
			
			logger.info(String.format("Downloading from %s to %s", remotePath, localPath));
			
			FileOutputStream out = new FileOutputStream(localPath);
			
			client.setRestartOffset(0);
			InputStream in = client.retrieveFileStream(remotePath);
			
			byte[] buffer = new byte[1024];
			
			int len;
			int total = 0;
			
		    while((len=in.read(buffer))>0) {
		    	total += len;
		    	out.write(buffer,0,len);
		    }
		    
		    logger.info("Index Size in bytes : " + total);
		    
		    in.close();
		    out.close();
		    client.completePendingCommand();
		    
			index = new File(localPath);
			
			if (! index.isFile()) {
				throw new IOException("Could not save the index file locally");
			}
			
			logger.info("Saved " + index.getAbsolutePath());
			logger.info("File size " + index.length());
			
		}
		
		logger.info("returning index " + index.getAbsolutePath());
		return index;
		
	}


}
