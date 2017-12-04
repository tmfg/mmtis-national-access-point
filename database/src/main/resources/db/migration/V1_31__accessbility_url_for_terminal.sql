-- Add new url for accessibility info
ALTER TYPE terminal_information
  ADD ATTRIBUTE "accessibility-info-url" VARCHAR(1024);
