INSERT INTO AREA(Name, Occupiable, Description)
    VALUES ("SHSS", "1", "School of Humanities and Social Sciences");

INSERT INTO AREA(Name, Occupiable, Description)
  VALUES ("SST", "1", "School of Science and Technology");

INSERT INTO AREA(Name, Occupiable, Description)
  VALUES ("SENG", "1", "School of Engineering");

INSERT INTO POKEMON(Name, Description, Type)
    VALUES ("Pikachu", "Whenever Pikachu comes across something new, it blasts it with a jolt of electricity. If you come across a blackened berry, it's evidence that this Pokémon mistook the intensity of its charge.", "Electric");

INSERT INTO POKEMON(Name, Description, Type)
  VALUES ("Pichu", "Pichu charges itself with electricity more easily on days with thunderclouds or when the air is very dry. You can hear the crackling of static electricity coming off this Pokémon.", "Electric");

INSERT INTO POKEMON(Name, Description, Type)
  VALUES ("Bulbasaur", "Some long description", "Grass/Poison");

INSERT INTO POKEMON(Name, Description, Type)
  VALUES ("Ivysaur", "Some long description", "Grass/Poison");

INSERT INTO PKM_WILD(Name, Area_name)
    VALUE ("Pikachu", "SST");

INSERT INTO PKM_WILD(Name, Area_name)
  VALUE ("Pichu", "SST");

INSERT INTO PKM_WILD(Name, Area_name)
    VALUE("Bulbasaur", "SST");

INSERT INTO PKM_WILD(Name, Area_name)
  VALUE("Ivysaur", "SST");



INSERT INTO PKM_WILD(Name, Area_name)
  VALUE ("Pikachu", "SHSS");

INSERT INTO PKM_WILD(Name, Area_name)
  VALUE ("Pichu", "SENG");