select o.genus, o.species, o.common_name, o.organism_id as ID, op.value as taxonID
from organism o, organismprop op, cvterm c
where op.type_id = c.cvterm_id 
and c.name = 'taxonId'
and o.organism_id = op.organism_id
order by o.common_name
