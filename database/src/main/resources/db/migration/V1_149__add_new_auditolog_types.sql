-- Add three new types to auditlog.
ALTER TYPE auditlog_event_type ADD VALUE 'add-member-to-operator';
ALTER TYPE auditlog_event_type ADD VALUE 'add-user-to-operator';
ALTER TYPE auditlog_event_type ADD VALUE 'remove-member-from-operator';