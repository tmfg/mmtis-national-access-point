-- TIS VACO eventually produces results in form of downloadable links based on requested processing. To support this
-- efficiently, this column allows us to capture the outputs and then upon request further process them.
-- While practically there's only one interesting result among the generated links, we store the entire links
-- object as JSON string to allow for better control on FINAP side on choosing what to serve back.
-- This column remains null until there's more than "self" links available
ALTER TABLE "gtfs_package" ADD COLUMN "tis-result-links" TEXT;