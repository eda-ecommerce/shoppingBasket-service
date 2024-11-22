# create databases
CREATE DATABASE IF NOT EXISTS `cs` ;
CREATE DATABASE IF NOT EXISTS `sb`;
CREATE DATABASE IF NOT EXISTS `offfer`;
CREATE DATABASE IF NOT EXISTS `pd`;
CREATE DATABASE IF NOT EXISTS `od`;

# create root user and grant rights
use mysql;
CREATE USER 'root'@'%' IDENTIFIED WITH caching_sha2_password BY 'verysecurepassword';
CREATE USER 'cs'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'cs-password';
CREATE USER 'cs'@'%' IDENTIFIED WITH caching_sha2_password BY 'cs-password';
CREATE USER 'sb'@'localhost' IDENTIFIED BY 'sb-password';
CREATE USER 'sb'@'%' IDENTIFIED BY 'sb-password';
CREATE USER 'offer'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'offfer-password';
CREATE USER 'offer'@'%' IDENTIFIED WITH caching_sha2_password BY 'offfer-password';
CREATE USER 'pd'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'pd-password';
CREATE USER 'pd'@'%' IDENTIFIED WITH caching_sha2_password BY 'pd-password';
CREATE USER 'od'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'od-password';
CREATE USER 'od'@'%' IDENTIFIED WITH caching_sha2_password BY 'od-password';

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH privileges ;

GRANT ALL PRIVILEGES ON cs.* TO 'cs'@'localhost';
GRANT ALL PRIVILEGES ON cs.* TO 'cs'@'%';
GRANT ALL PRIVILEGES on sb.* TO 'sb'@'localhost';
GRANT ALL PRIVILEGES on sb.* TO 'sb'@'%';
GRANT ALL PRIVILEGES on offer.* TO 'offer'@'localhost';
GRANT ALL PRIVILEGES on offer.* TO 'offer'@'%';
GRANT ALL PRIVILEGES on pd.* TO 'pd'@'localhost';
GRANT ALL PRIVILEGES on pd.* TO 'pd'@'%';
GRANT ALL PRIVILEGES on od.* TO 'od'@'localhost';
GRANT ALL PRIVILEGES on od.* TO 'od'@'%';

flush privileges;