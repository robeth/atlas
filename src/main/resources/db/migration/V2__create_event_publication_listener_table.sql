-- Create event_publication table for Spring Modulith
CREATE TABLE event_publication (
    id VARCHAR(255) NOT NULL,
    completion_date TIMESTAMP,
    event_type VARCHAR(512) NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    serialized_event LONGTEXT NOT NULL,
    PRIMARY KEY (id)
);


-- Create index for event_publication
CREATE INDEX idx_event_publication_listener_id ON event_publication(listener_id);
CREATE INDEX idx_event_publication_completion_date ON event_publication(completion_date);
