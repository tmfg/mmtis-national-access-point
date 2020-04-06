-- Add chouette - netex conversion erros to netex-conversion table
ALTER TABLE "netex-conversion"
    ADD COLUMN "input-file-error" TEXT,
    ADD COLUMN "validation-file-error" TEXT;