package org.genedb.crawl;

import java.io.IOException;

import org.genedb.crawl.business.TabixReader;
import org.genedb.crawl.elasticsearch.index.gff.GFFFeature;

import junit.framework.TestCase;

public class GFFFeatureTest extends TestCase {
	
	String s = "Pf3D7_01	chado	repeat_region	1	583	.	+	.	ID=Pfalciparum_REP_20;isObsolete=false;feature_id=851;timelastmodified=21.10.2007+12:25:59+BST;comment=telomeric+repeat";
	String s2 = "Pf3D7_01	chado	polypeptide	134587	139491	.	-	.	ID=PFA0155c:pep;Derives_from=PFA0155c:mRNA;Dbxref=OrthoMCLDB:PFA0155c%2CPlasmoDB:PFA0155c%2CUniProtKB:Q8I2A9;colour=10;private=ESG+predicted+GO+terms+present+-+manually+reviewed+-+dpd+08/10/09;timelastmodified=08.10.2009+11:40:44+BST;blastp_file=/nfs/pathdata/Plasmodium/falciparum/3D7/workshop/DATABASES/apicomplexans:blastp/MAL1.embl.seq.00178.out%2Cblastp/MAL1.new.tab.seq.00002.out%2C%uniprot:blastp/MAL1.embl.seq.00031.out;feature_id=260;blastp%2Bgo_file=true;isObsolete=false;orthologous_to=Pvivax:PVX_081600+link%3DPVX_081600:pep+type%3Dorthologous_to%2C+Pberghei:PBANKA_021000+link%3DPBANKA_021000:pep+type%3Dorthologous_to%2C+Pyoelii:PY06525+link%3DPY06525:pep+type%3Dorthologous_to%2C+Pchabaudi:PCAS_020840+link%3DPCAS_020840:pep+type%3Dorthologous_to%2C+Pknowlesi:PKH_021030+link%3DPKH_021030:pep+type%3Dorthologous_to%3B+cluster_name%3DPlasmodium:ORTHOMCL938%3B+program%3DOrthoMCL%3B+rank%3D0;gO=aspect%3DP%3BGOid%3DGO:0006887%3Bterm%3Dexocytosis%3Bdb_xref%3DPMID:19435743%3Bdate%3D20090805%3Bevidence%3DInferred+from+Electronic+Annotation%3Battribution%3DGeneDB_Pfalciparum%3Bautocomment%3DFrom+GO+association+file%2Caspect%3DP%3BGOid%3DGO:0015031%3Bterm%3Dprotein+transport%3Bdb_xref%3DPMID:19435743%3Bdate%3D20090805%3Bevidence%3DInferred+from+Electronic+Annotation%3Battribution%3DGeneDB_Pfalciparum%3Bautocomment%3DFrom+GO+association+file;product=term%3Dconserved+Plasmodium+protein%2C+unknown+function%3B;fasta_file=/nfs/pathdata/Plasmodium/falciparum/3D7/workshop/DATABASES/apicomplexans:fasta/MAL1.embl.seq.00031.out%2C%uniprot:fasta/MAL1.embl.seq.00178.out%2Cfasta/MAL1.new.tab.seq.00002.out;polypeptide_domain=iprscan%3BInterPro:IPR014812+:%09Vps51/Vps67%3BPfam:PF08700%3B%3Bscore%3D6.2E-27%3Bquery+116-202%3Bdescription%3DVps51/Vps67%2Ciprscan%3BPANTHER:PTHR15954%3B%3Bscore%3D1.1E-29%3Bquery+104-290%3Bdescription%3Dnull;gPI_anchor_cleavage_site=%3Bquery+1603-1603%3BGPI_cleavage_site_score%3D0.78000003;translation=menknnrrknvssmlynyynfearnkveeleecrdnnrnnidrinniskmnnsvddnnffdkdenikekstilkssdnnsinlridenykniendikqcneenvmdkmnyidefdmncsnfnvnnyfkellekssmydlinkskkvdkeikqndscmqtliyenynkfinaadalvllkekfkcvkdkmkeinnhldyidkesnflnndifknyekienlieikkllnhineimkipeymysyilekkymkslkmfikvipffhknkdlvifqnlyldcnnlaniachfflkklnkeknvsklpvqshkkddkkcyskndvksdirdnnkcvhhldesknhmdynknyvdegdkcfyffdnnnleesfeslhsyvlsseevaeclnlvlsygmdrkeikklylknridclkylmynifslknhgffffvrkaqdfkssffdhnyqefhidkqnkkneynknngnteyfynnifenifilcykhllyfffsllenyekifmkrnnfinnfdghkgcfisffykegehpddknlnnnidylrylinnldesfqdksgktrcehfvmsydnnknknnnknnnnnnknnnnnnknnnnnnknnnndrytylhksfdlskklllndllnidddnkiiealvyiffkvlckitidyiymfnppiklivkllktlidnvnghnynnnnnnnniyykdkilydmnrfikkiyyvllklyfynlcfhiniylytyfqkcdekklidilesknelshyillklcltfmdfepfyveiklennfyvkhffnfviiyleslsrhidsfiyyfvcvhekssfiqegnvlkyikdehkkecskdatfsnnkgddnghhnyihgkedihgkedihsnedihgnkdihsnedihgnedihsnedihgkedihsnedihgkedihrnedihrnedihgkedihsnedihsnedihsnthfdnqlskdfpyfmyepadniiyedkenifthknlyymideentihckhrkkkdepfcgfykklknylyssvyvnhvhmkhilveikkymkmeytiflthitgslkfgkikkikeiyfllclvwifhnikregvskifnvitdmykeandlingckridmgcsldyllhkdiypfiketekekekenkknkklklkdgdsinydneqgleytngahifinknyekgedkkiknltftyseeekknklyefnfdnnnicgddnnicgddnniydddynicgddnniydddyniyddddnniydnnhnnnicdnysherntkniiikmtkdeeekrqnindknrkkkyvigisnfvkykfhekcneltnvfisyyintisnhiksyvekdtyeedtksnlvsnnfvycmkhidifykylkyfvyqnksasvinfegqereyvfsdmyekieeefqelgkkylqdkcmrlgknnngrtdigiveqnnsvlykdnnlvvqnnnmldksnhmvdhnnhirgrkyimndkeynednkmsngnrkeqimennnllfdmnkdeknlemymyklfmlkmknyrkhlpteinkillliikivfknymeyirkchmnekklykmqidffffyhclkhyipcddenvlfvilnevlinakgrirgiqnkrdeddgassyqgylllddihidfeenklfilkmfk";
	
	public void test1() {
		
		GFFFeature f = new GFFFeature(s); 
		
		
		assertEquals(f.start, 1);
		assertEquals(f.end, 583);
		assertEquals(f.attributes.map.get("comment"), "telomeric repeat");
		
		
		GFFFeature f2 = new GFFFeature(s2); 
		
		assertEquals(f2.start, 134587);
		assertEquals(f2.end, 139491);
		
		
		
	}
	
	

}
