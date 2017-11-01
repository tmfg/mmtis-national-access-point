-- Add missing sub-types for transport-service

ALTER TYPE transport_service_subtype ADD VALUE 'terminal';
ALTER TYPE transport_service_subtype ADD VALUE 'rentals';
ALTER TYPE transport_service_subtype ADD VALUE 'parking';
ALTER TYPE transport_service_subtype ADD VALUE 'brokerage';