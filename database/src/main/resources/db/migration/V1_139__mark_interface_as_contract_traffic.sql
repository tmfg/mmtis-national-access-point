-- Some of the transport services are contract services and some of them are commercial. Our transit change algorithm is not interested in
-- contract traffic. So add column that stores information about that

ALTER TABLE "transport-service"
  ADD COLUMN "commercial-traffic?" boolean DEFAULT TRUE;