# ads_txt_crawler

###The main code starts at Application.java and it can be tweaked to run either publisher sourcing or ads.txt sourcing


##Sourcing publisher data
  The ads.txt crawler is a utility to taken in a list of publishers from a file domains_list.txt
  This data is sourced into mysql using a FixedThreadPool and uses DBCP2 for mysql thread pool to source the data

##Sourcing ads txt data 
  The ads.txt crawler has another utility which again runs on ThreadPools similar to what we did in source publishers code, to crawl the urls sourced in publishers collection and then downloading the ads.txt for each of the publisher's url by first checking if the url is http or https and then downloading it. Redirects are disabled. 
  Once the file is downloaded, it parsed line by line and sourced into the advertisers collection 
  
  
  ###The code for Sourcing ads.txt has a problem of exhausting mysql connections 
  
  ##Schema: 
  
  The inital schema designed according to Normalized form is as follows, 
 
>CREATE TABLE `publishers` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL,
  `processed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueUrl` (`url`)
)

>CREATE TABLE `advertisers` (
     id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(30) NOT NULL,
     accounttypeid MEDIUMINT NOT NULL,
     UNIQUE KEY uniqueName(name),
     FOREIGN KEY (accounttypeid)
        REFERENCES accounttypes(id)
        ON DELETE CASCADE
)

>CREATE TABLE accounttypes (
	id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(30) NOT NULL,
    UNIQUE KEY uniqueType(type)	
);

>CREATE TABLE tags (
	id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    publisherid MEDIUMINT NOT NULL,
    advertiserid MEDIUMINT NOT NULL
);

##Due to lack of time this has been restricted to, 

>CREATE TABLE `publishers` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL,
  `processed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueUrl` (`url`)
)

>CREATE TABLE `advertisers` (
  `id` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `advertiserId` varchar(30) NOT NULL,
  `accountType` varchar(30) NOT NULL,
  `tagId` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
)





