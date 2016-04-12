use squire;

create table Projects (
	PID integer unsigned not null primary key auto_increment,
	pname varchar(30),
	location varchar(40)
);

create table PFLines (
	pflid integer unsigned not null primary key auto_increment,
	nextid integer unsigned DEFAULT NULL,
	text varchar(255),
	lastEditor integer unsigned DEFAULT NULL,
	timeEdited timestamp,
	foreign key (lastEditor) references Users(userID),
	foreign key (nextid) references PFLines(pflid)
);

CREATE TABLE PDirs (
  pdid integer unsigned NOT NULL AUTO_INCREMENT,
  pdname varchar(30),
  pid integer unsigned DEFAULT NULL,
  parentid integer unsigned DEFAULT NULL,
  PRIMARY KEY (pdid),
  FOREIGN KEY (parentid) REFERENCES PDirs (pdid),
  FOREIGN KEY (pid) REFERENCES Projects(PID)
);

CREATE TABLE PFiles (
  pfid int(11) unsigned NOT NULL AUTO_INCREMENT,
  pfname varchar(30),
  pid int unsigned DEFAULT NULL,
  pdid int(11) unsigned DEFAULT NULL,
  pflhead int unsigned DEFAULT NULL, 
  timeCreated timestamp,
  creatorID int UNSIGNED DEFAULT NULL,
  lockuser int UNSIGNED DEFAULT NULL,
  PRIMARY KEY (pfid),
  FOREIGN KEY (pflhead) REFERENCES PFLines (pflid),
  FOREIGN KEY (pdid) REFERENCES PDirs (pdid),
  FOREIGN KEY (pid) REFERENCES Projects (PID),
  FOREIGN KEY (creatorID) REFERENCES Users (userID),
  FOREIGN KEY (lockuser) REFERENCES Users (userID)
);

create table ProjectAccess (
	PID integer unsigned not null,
	userID integer unsigned not null,
	primary key(PID, userID),
	foreign key (PID) references Projects(PID),
	foreign key (userID) references Users(userID)
);

create table FileEdits (
	edID int unsigned not null auto_increment,
	pfID int unsigned NOT NULL,
	userID int unsigned,
	timeof timestamp,
	PRIMARY KEY(edID),
	FOREIGN KEY (pfID) REFERENCES PFiles (pfid),
	FOREIGN KEY (userID) REFERENCES Users (userID)
);

delimiter //
CREATE PROCEDURE PFLTraverser 
(
    in inputNo int
) 
BEGIN 
    declare final_id int default NULL;
    CREATE TEMPORARY TABLE IF NOT EXISTS my_temp_table select * from PFLines where 1 = 0;
    INSERT INTO my_temp_table(SELECT * FROM PFLines WHERE pflid = inputNo);
	SELECT nextID 
    INTO final_id 
    FROM PFLines
    WHERE pflid = inputNo;
    WHILE ( final_id is not null) DO
    INSERT INTO my_temp_table(SELECT * FROM PFLines WHERE pflid = final_id);
    SELECT nextID 
        INTO final_id 
        FROM PFLines
        WHERE pflid = final_id;
        
    end while;
    SELECT * FROM my_temp_table;
    DROP table my_temp_table;
END//
delimiter ;

