ALTER TABLE "netex-conversion"
    ADD CONSTRAINT unique_service_id_external_interface_description_id UNIQUE("transport-service-id", "external-interface-description-id");
