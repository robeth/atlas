-- Initialize MySQL database for Atlas application
-- This script runs on container startup

-- Set character set and collation
ALTER DATABASE atlas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create additional user if needed
-- CREATE USER 'atlas_readonly'@'%' IDENTIFIED BY 'readonly123';
-- GRANT SELECT ON atlas.* TO 'atlas_readonly'@'%';

-- Ensure proper timezone
SET time_zone = '+00:00';

-- Log initialization
SELECT 'Atlas database initialized successfully' AS message;
