CREATE TABLE "associated-service-operators"
(
  "service-id" INTEGER NOT NULL REFERENCES "transport-service" (id) ON DELETE CASCADE,
  "operator-id" INTEGER NOT NULL REFERENCES "transport-operator" (id) ON DELETE CASCADE,
  PRIMARY KEY("service-id", "operator-id")
);