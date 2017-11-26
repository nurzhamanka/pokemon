create table ABILITY
(
	Name varchar(45) not null
		primary key,
	constraint Name_UNIQUE
		unique (Name)
)
;

create table AREA
(
	Name varchar(45) not null
		primary key,
	Occupiable tinyint(1) not null,
	Description text null,
	constraint Name_UNIQUE
		unique (Name)
)
;

create table BATTLE
(
	ID int not null,
	Trainer1_ID int not null,
	Trainer2_ID int not null,
	Winner_ID int null,
	Gym_name varchar(45) not null,
	primary key (ID, Trainer1_ID, Trainer2_ID)
)
;

create index Gym_name_idx
	on BATTLE (Gym_name)
;

create index Trainer1_ID_idx
	on BATTLE (Trainer1_ID)
;

create index Trainer2_ID_idx
	on BATTLE (Trainer2_ID)
;

create index Winner_ID_idx
	on BATTLE (Winner_ID)
;

create table GYM
(
	Name varchar(45) not null
		primary key,
	Area_name varchar(45) not null,
	Leader_ID int null,
	constraint fk_Area_name_GYM
		foreign key (Area_name) references AREA (Name),
	constraint fk_Leader_ID_GYM
		foreign key (Leader_ID) references pokemondb.TRAINER (ID)
)
;

create index Area_name_idx
	on GYM (Area_name)
;

create index Leader_ID_idx
	on GYM (Leader_ID)
;

alter table BATTLE
	add constraint fk_Gym_name_BATTLE
		foreign key (Gym_name) references GYM (Name)
;

create table HAS
(
	Pokemon_name varchar(45) not null,
	Ability_name varchar(45) not null,
	primary key (Pokemon_name, Ability_name),
	constraint fk_Ability_name_HAS
		foreign key (Ability_name) references ABILITY (Name)
)
;

create index Ability_name_idx
	on HAS (Ability_name)
;

create table PKM_OWNED
(
	Name varchar(45) not null,
	Trainer_ID int not null,
	Nickname varchar(45) not null,
	primary key (Name, Trainer_ID, Nickname)
)
;

create index Name_idx
	on PKM_OWNED (Name)
;

create index Trainer_ID_idx
	on PKM_OWNED (Trainer_ID)
;

create table PKM_WILD
(
	Date_time datetime not null,
	Name varchar(45) not null,
	Aggressiveness int null,
	Stamina int null,
	Area_name varchar(45) not null,
	Trainer_ID int null,
	primary key (Date_time, Name),
	constraint fk_Area_name_WILD
		foreign key (Area_name) references AREA (Name)
)
;

create index fk_Area_name_idx
	on PKM_WILD (Area_name)
;

create index Name_idx
	on PKM_WILD (Name)
;

create index Trainer_ID_idx
	on PKM_WILD (Trainer_ID)
;

create table POKEMON
(
	Name varchar(20) not null
		primary key,
	Description mediumtext not null,
	constraint Name_UNIQUE
		unique (Name)
)
;

alter table HAS
	add constraint fk_Pokemon_name_HAS
		foreign key (Pokemon_name) references POKEMON (Name)
;

alter table PKM_OWNED
	add constraint fk_Name_OWNED
		foreign key (Name) references POKEMON (Name)
;

alter table PKM_WILD
	add constraint fk_Name_WILD
		foreign key (Name) references POKEMON (Name)
;

create table POKEMON_TYPE
(
	Pkm_name varchar(45) not null,
	Type varchar(45) not null,
	primary key (Pkm_name, Type),
	constraint fk_Pkm_name_PKM_TYPE
		foreign key (Pkm_name) references POKEMON (Name)
)
;

create index Name_idx
	on POKEMON_TYPE (Pkm_name)
;

create table TRAINER
(
	ID int auto_increment
		primary key,
	FName varchar(45) not null,
	LName varchar(45) not null,
	Area_name varchar(45) not null,
	constraint ID_UNIQUE
		unique (ID),
	constraint fk_Area_name_TRAINER
		foreign key (Area_name) references AREA (Name)
)
;

create index Area_name_idx
	on TRAINER (Area_name)
;

alter table BATTLE
	add constraint fk_Trainer1_ID_BATTLE
		foreign key (Trainer1_ID) references TRAINER (ID)
;

alter table BATTLE
	add constraint fk_Trainer2_ID_BATTLE
		foreign key (Trainer2_ID) references TRAINER (ID)
;

alter table BATTLE
	add constraint fk_Winner_ID_BATTLE
		foreign key (Winner_ID) references TRAINER (ID)
;

alter table PKM_OWNED
	add constraint fk_Trainer_ID_OWNED
		foreign key (Trainer_ID) references TRAINER (ID)
;

alter table PKM_WILD
	add constraint fk_Trainer_ID_WILD
		foreign key (Trainer_ID) references TRAINER (ID)
;

