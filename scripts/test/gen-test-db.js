// eval(fs.readFileSync('~/proj/github/briefly/scripts/test/gen-test-db.js').toString('UTF-8'));
// generateTestDb({authorCount: 100, originsCount: 40, bookCount: 100, fileName: "/tmp/s1.sql"})
// java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.RunScript -url jdbc:h2:/tmp/d2 -user sa -script /Users/alex/proj/github/briefly/briefly-website/src//main/resources/brieflyWebsite/sql/eolaire/eolaire-schema.sql
// rlwrap java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.Shell -url jdbc:h2:/tmp/d2 -user sa

var fs = require('fs');

function rand(from, to) {
    return Math.floor(Math.random() * (to - from)) + from;
}

function appendRandName(result, parts, minPartsNum, maxPartsNum) {
    minPartsNum = minPartsNum || 1;
    maxPartsNum = maxPartsNum || 1;
    var partCount = rand(minPartsNum, maxPartsNum + 1);
    var i;
    for (i = 0; i < partCount; ++i) {
        var part = parts[rand(0, parts.length)];
        if (part.length === 0) { continue; }
        result.push(part);
    }
}

function randName(parts, minPartsNum, maxPartsNum) {
    var result = [];
    appendRandName(result, parts, minPartsNum, maxPartsNum);
    return result.join(" ");
}

var PERSON_NAME_PREFIX = [
    "", "", "", "", "", "", "", "", "", "Jr.", "Sr.", "Prof.", "M.D."
];

var PERSON_FIRST_NAME = [
/* Male Names */
'Robert', 'James', 'John', 'William', 'Richard', 'Charles', 
'Donald', 'George', 'Thomas', 'Joseph', 'David', 'Edward', 
'Ronald', 'Paul', 'Kenneth', 'Frank', 'Raymond', 'Jack', 
'Harold', 'Billy', 'Gerald', 'Walter', 'Jerry', 'Joe', 
'Eugene', 'Henry', 'Bobby', 'Arthur', 'Carl', 'Larry', 
'Ralph', 'Albert', 'Willie', 'Fred', 'Michael', 'Lawrence', 
'Harry', 'Howard', 'Roy', 'Norman', 'Roger', 'Daniel', 
'Louis', 'Earl', 'Gary', 'Clarence', 'Anthony', 'Francis', 
'Wayne', 'Marvin', 'Ernest', 'Leonard', 'Herbert', 'Melvin', 
'Stanley', 'Leroy', 'Don', 'Peter', 'Jimmy', 'Alfred', 
'Dale', 'Bill', 'Samuel', 'Bernard', 'Ray', 'Gene', 
'Philip', 'Russell', 'Frederick', 'Franklin', 'Dennis', 'Douglas', 
'Jimmie', 'Gordon', 'Andrew', 'Theodore', 'Floyd', 'Johnny', 
'Allen', 'Glenn', 'Bruce', 'Edwin', 'Lee', 'Lloyd', 
'Bob', 'Clifford', 'Leon', 'Leo', 'Clyde', 'Eddie', 
'Vernon', 'Martin', 'Alvin', 'Jim', 'Herman', 'Lewis', 
'Harvey', 'Tommy', 'Vincent', 'Charlie', 'Warren', 'Jerome', 
'Jesse', 'Patrick', 'Stephen', 'Curtis', 'Arnold', 'Gilbert', 
'Elmer', 'Lester', 'Duane', 'Phillip', 'Cecil', 'Tom', 
'Alan', 'Milton', 'Jackie', 'Victor', 'Johnnie', 'Roland', 
'Benjamin', 'Glen', 'Chester', 'Calvin', 'Keith', 'Dean', 
'Sam', 'Wallace', 'Claude', 'Maurice', 'Willard', 'Manuel', 
'Jose', 'Leslie', 'Marion', 'Edgar', 'Max', 'Hugh', 
'Oscar', 'Allan', 'Virgil', 'Jessie', 'Darrell', 'Terry', 
'Everett', 'Wesley', 'Ted', 'Freddie', 'Jay', 'Dick', 
'Hubert', 'Nicholas', 'Billie', 'Rodney', 'Lowell', 'Dan', 
'Neil', 'Sidney', 'Homer', 'Delbert', 'Tony', 'Morris', 
'Lyle', 'Wilbur', 'Luther', 'Earnest', 'Ronnie', 'Bennie', 
'Joel', 'Ben', 'Steve', 'Rudolph', 'Willis', 'Horace', 
'Lonnie', 'Mike', 'Junior', 'Carroll', 'Karl', 'Guy', 
'Roosevelt', 'Otis', 'Mark', 'Nathaniel', 'Danny', 'Alton', 
'Marshall', 'Clayton', 'Alexander', 'Benny', 'Clifton', 'Archie', 
'Oliver', 'Clinton', 'Barry', 'Juan', 'Salvatore', 'Nelson', 
'Jon', 'Alex',

/* Female names */
'Mary', 'Betty', 'Barbara', 'Shirley', 'Patricia', 'Dorothy', 
'Joan', 'Margaret', 'Nancy', 'Helen', 'Carol', 'Joyce', 
'Doris', 'Ruth', 'Virginia', 'Marilyn', 'Elizabeth', 'Jean', 
'Frances', 'Beverly', 'Lois', 'Alice', 'Donna', 'Martha', 
'Dolores', 'Janet', 'Phyllis', 'Norma', 'Carolyn', 'Evelyn', 
'Gloria', 'Anna', 'Marie', 'Ann', 'Mildred', 'Rose', 
'Peggy', 'Geraldine', 'Catherine', 'Judith', 'Louise', 'Janice', 
'Marjorie', 'Annie', 'Ruby', 'Eleanor', 'Jane', 'Sandra', 
'Irene', 'Wanda', 'Elaine', 'June', 'Joanne', 'Rita', 
'Florence', 'Delores', 'Lillian', 'Marlene', 'Edna', 'Sarah', 
'Patsy', 'Lorraine', 'Thelma', 'Josephine', 'Juanita', 'Bonnie', 
'Arlene', 'Gladys', 'Joann', 'Sally', 'Charlotte', 'Kathleen', 
'Audrey', 'Pauline', 'Wilma', 'Sylvia', 'Theresa', 'Jacqueline', 
'Clara', 'Ethel', 'Loretta', 'Grace', 'Sharon', 'Edith', 
'Lucille', 'Emma', 'Bernice', 'Marion', 'Linda', 'Jo', 
'Anne', 'Hazel', 'Roberta', 'Carole', 'Darlene', 'Esther', 
'Katherine', 'Ellen', 'Laura', 'Julia', 'Rosemary', 'Jeanette', 
'Marian', 'Willie', 'Beatrice', 'Margie', 'Billie', 'Vivian', 
'Eva', 'Kathryn', 'Elsie', 'Judy', 'Eileen', 'Anita', 
'Diane', 'Bertha', 'Susan', 'Maria', 'Maxine', 'Ida', 
'Yvonne', 'Ella', 'Lillie', 'Constance', 'Sue', 'Bobbie', 
'Georgia', 'Jeanne', 'Christine', 'Sara', 'Alma', 'Bessie', 
'Agnes', 'Vera', 'Nellie', 'Kay', 'Jessie', 'Karen', 
'Lucy', 'Mattie', 'Gertrude', 'Rosa', 'Minnie', 'Gail', 
'Connie', 'Geneva', 'Viola', 'Velma', 'Marcia', 'Leona', 
'Myrtle', 'Violet', 'Rosalie', 'Harriet', 'Annette', 'Naomi', 
'Charlene', 'Pearl', 'Joy', 'Mae', 'Suzanne', 'Claire', 
'Rebecca', 'Faye', 'Carrie', 'Pat', 'Dora', 'Rachel', 
'Rosie', 'Emily', 'Eunice', 'Maureen', 'Alberta', 'Cora', 
'Stella', 'Lula', 'Sheila', 'Caroline', 'Glenda', 'Verna', 
'Lena', 'Lola', 'Myrna', 'Hattie', 'Rosemarie', 'Ramona', 
'Gwendolyn', 'Jeannette', 'Erma', 'Genevieve', 'Cynthia', 'Nina', 
'Patty', 'Fannie', 'Diana', 'Jennie', 'Hilda', 'Marguerite', 
'Daisy', 'Johnnie', 
];

var PERSON_LAST_NAME = [
'SHEA', 'ROUSE', 'HARTLEY', 'MAYFIELD', 'ELDER', 'RANKIN', 'HANNA', 'COWAN', 'LUCERO', 'ARROYO', 
'SLAUGHTER', 'HAAS', 'OCONNELL', 'MINOR', 'KENDRICK', 'SHIRLEY', 'KENDALL', 'BOUCHER', 'ARCHER', 
'BOGGS', 'ODELL', 'DOUGHERTY', 'ANDERSEN', 'NEWELL', 'CROWE', 'WANG', 'FRIEDMAN', 'BLAND', 
'SWAIN', 'HOLLEY', 'FELIX', 'PEARCE', 'CHILDS', 'YARBROUGH', 'GALVAN', 'PROCTOR', 'MEEKS', 
'LOZANO', 'MORA', 'RANGEL', 'BACON', 'VILLANUEVA', 'SCHAEFER', 'ROSADO', 'HELMS', 'BOYCE', 
'GOSS', 'STINSON', 'SMART', 'LAKE', 'IBARRA', 'HUTCHINS', 'COVINGTON', 'REYNA', 'GREGG', 
'WERNER', 'CROWLEY', 'HATCHER', 'MACKEY', 'BUNCH', 'WOMACK', 'POLK', 'JAMISON', 'DODD', 
'CHILDRESS', 'CHILDERS', 'CAMP', 'VILLA', 'DYE', 'SPRINGER', 'MAHONEY', 'DAILEY', 'BELCHER', 
'LOCKHART', 'GRIGGS', 'COSTA', 'CONNOR', 'BRANDT', 'WINTER', 'WALDEN', 'MOSER', 'TRACY', 
'TATUM', 'MCCANN', 'AKERS', 'LUTZ', 'PRYOR', 'LAW', 'OROZCO', 'MCALLISTER', 'LUGO', 
'DAVIES', 'SHOEMAKER', 'MADISON', 'RUTHERFORD', 'NEWSOME', 'MAGEE', 'CHAMBERLAIN', 'BLANTON', 'SIMMS', 
'GODFREY', 'FLANAGAN', 'CRUM', 'CORDOVA', 'ESCOBAR', 'DOWNING', 'SINCLAIR', 'DONAHUE', 'KRUEGER', 
'MCGINNIS', 'GORE', 'FARRIS', 'WEBBER', 'CORBETT', 'ANDRADE', 'STARR', 'LYON', 'YODER', 
'HASTINGS', 'MCGRATH', 'SPIVEY', 'KRAUSE', 'HARDEN', 'CRABTREE', 'KIRKPATRICK', 'HOLLIS', 'BRANDON', 
'ARRINGTON', 'ERVIN', 'CLIFTON', 'RITTER', 'MCGHEE', 'BOLDEN', 'MALONEY', 'GAGNON', 'DUNBAR', 
'PONCE', 'PIKE', 'MAYES', 'HEARD', 'BEATTY', 'MOBLEY', 'KIMBALL', 'BUTTS', 'MONTES', 
'HERBERT', 'GRADY', 'ELDRIDGE', 'BRAUN', 'HAMM', 'GIBBONS', 'SEYMOUR', 'MOYER', 'MANLEY', 
'HERRON', 'PLUMMER', 'ELMORE', 'CRAMER', 'GARY', 'RUCKER', 'HILTON', 'BLUE', 'PIERSON', 
'FONTENOT', 'FIELD', 'RUBIO', 'GRACE', 'GOLDSTEIN', 'ELKINS', 'WILLS', 'NOVAK', 'JOHN', 
'HICKEY', 'WORLEY', 'GORMAN', 'KATZ', 'DICKINSON', 'BROUSSARD', 'FRITZ', 'WOODRUFF', 'CROW', 
'CHRISTOPHER', 'BRITTON', 'FORREST', 'NANCE', 'LEHMAN', 'BINGHAM', 'ZUNIGA', 'WHALEY', 'SHAFER', 
'COFFMAN', 'STEWARD', 'DELAROSA', 'NIX', 'NEELY', 'NUMBERS', 'MATA', 'MANUEL', 'DAVILA', 
'MCCABE', 'KESSLER', 'EMERY', 'BOWLING', 'HINKLE', 'WELSH', 'PAGAN', 'GOLDBERG', 'GOINS', 
'CROUCH', 'CUEVAS', 'QUINONES', 'MCDERMOTT', 'HENDRICKSON', 'SAMUELS', 'DENTON', 'BERGERON', 'LAM', 
'IVEY', 'LOCKE', 'HAINES', 'THURMAN', 'SNELL', 'HOSKINS', 'BYRNE', 'MILTON', 'WINSTON', 
'ARTHUR', 'ARIAS', 'STANFORD', 'ROE', 'CORBIN', 'BELTRAN', 'CHAPPELL', 'HURT', 'DOWNEY', 
'DOOLEY', 'TUTTLE', 'COUCH', 'PAYTON', 'MCELROY', 'CROCKETT', 'GROVES', 'CLEMENT', 'LESLIE', 
'CARTWRIGHT', 'DICKEY', 'MCGILL', 'DUBOIS', 'MUNIZ', 'ERWIN', 'SELF', 'TOLBERT', 'DEMPSEY', 
'CISNEROS', 'SEWELL', 'LATHAM', 'GARLAND', 'VIGIL', 'TAPIA', 'STERLING', 'RAINEY', 'NORWOOD', 
'LACY', 'STROUD', 'MEADE', 'AMOS', 'TIPTON', 'LORD', 'KUHN', 'HILLIARD', 'BONILLA', 
'TEAGUE', 'COURTNEY', 'GUNN', 'HO', 'GREENWOOD', 'CORREA', 'REECE', 'WESTON', 'POE', 
'TRENT', 'PINEDA', 'PHIPPS', 'FREY', 'KAISER', 'AMES', 'PAIGE', 'GUNTER', 'SCHMITT', 
'MILLIGAN', 'ESPINOSA', 'CARLTON', 'BOWDEN', 'VICKERS', 'LOWRY', 'PRITCHARD', 'COSTELLO', 'PIPER', 
'MCCLELLAN', 'LOVELL', 'DREW', 'SHEEHAN', 'QUICK', 'HATCH', 'DOBSON', 'SINGH', 'JEFFRIES', 
'HOLLINGSWORTH', 'SORENSEN', 'MEZA', 'FINK', 'DONNELLY', 'BURRELL', 'BRUNO', 'TOMLINSON', 'COLBERT', 
'BILLINGS', 'RITCHIE', 'HELTON', 'SUTHERLAND', 'PEOPLES', 'MCQUEEN', 'GASTON', 'THOMASON', 'MCKINLEY', 
'GIVENS', 'CROCKER', 'VOGEL', 'ROBISON', 'DUNHAM', 'COKER', 'SWARTZ', 'KEYS', 'LILLY', 
'LADNER', 'HANNAH', 'WILLARD', 'RICHTER', 'HARGROVE', 'EDMONDS', 'BRANTLEY', 'ALBRIGHT', 'MURDOCK', 
'BOSWELL', 'MULLER', 'QUINTERO', 'PADGETT', 'KENNEY', 'DALY', 'CONNOLLY', 'PIERRE', 'INMAN', 
'QUINTANA', 'LUND', 'BARNARD', 'VILLEGAS', 'SIMONS', 'LAND', 'HUGGINS', 'TIDWELL', 'SANDERSON', 
'BULLARD', 'MCCLENDON', 'DUARTE', 'DRAPER', 'MEREDITH', 'MARRERO', 'DWYER', 'ABRAMS', 'STOVER', 
'GOODE', 'FRASER', 'CREWS', 'BERNAL', 'SMILEY', 'GODWIN', 'FISH', 'CONKLIN', 'MCNEAL', 
'BACA', 'ESPARZA', 'CROWDER', 'BOWER', 'NICHOLAS', 'CHUNG', 'BREWSTER', 'MCNEILL', 'DICK', 
'RODRIGUES', 'LEAL', 'COATES', 'RAINES', 'MCCAIN', 'MCCORD', 'MINER', 'HOLBROOK', 'SWIFT', 
'DUKES', 'CARLISLE', 'ALDRIDGE', 'ACKERMAN', 'STARKS', 'RICKS', 'HOLLIDAY', 'FERRIS', 'HAIRSTON', 
'SHEFFIELD', 'LANGE', 'FOUNTAIN', 'MARINO', 'DOSS', 'BETTS', 'KAPLAN', 'CARMICHAEL', 'BLOOM', 
'RUFFIN', 'PENN', 'KERN', 'BOWLES', 'SIZEMORE', 'LARKIN', 'DUPREE', 'JEWELL', 'SILVER', 
'SEALS', 'METCALF', 'HUTCHISON', 'HENLEY', 'FARR', 'CASTLE', 'MCCAULEY', 'HANKINS', 'GUSTAFSON', 
'DEAL', 'CURRAN', 'ASH', 'WADDELL', 'RAMEY', 'CATES', 'POLLOCK', 'MAJOR', 'IRVIN', 
'CUMMINS', 'MESSER', 'HELLER', 'DEWITT', 'LIN', 'FUNK', 'CORNETT', 'PALACIOS', 'GALINDO', 
'CANO', 'HATHAWAY', 'SINGER', 'PHAM', 'ENRIQUEZ', 'AARON', 'SALGADO', 'PELLETIER', 'PAINTER', 
'WISEMAN', 'BLOUNT', 'HAND', 'FELICIANO', 'TEMPLE', 'HOUSER', 'DOHERTY', 'MEAD', 'MCGRAW', 
'TONEY', 'SWAN', 'MELVIN', 'CAPPS', 'BLANCO', 'BLACKMON', 'WESLEY', 'THOMSON', 'MCMANUS', 
'FAIR', 'BURKETT', 'POST', 'GLEASON', 'RUDOLPH', 'OTT', 'DICKENS', 'CORMIER', 'VOSS', 
'RUSHING', 'ROSENBERG', 'HURD', 'DUMAS', 'BENITEZ', 'ARELLANO', 'STORY', 'MARIN', 'CAUDILL', 
'BRAGG', 'JARAMILLO', 'HUERTA', 'GIPSON', 'COLVIN', 'BIGGS', 'VELA', 'PLATT', 'CASSIDY', 
'TOMPKINS', 'MCCOLLUM', 'KAY', 'GABRIEL', 'DOLAN', 'DALEY', 'CRUMP', 'STREET', 'SNEED', 
'KILGORE', 'GROVE', 'GRIMM', 'DAVISON', 'BRUNSON', 'PRATER', 'MARCUM', 'DEVINE', 'KYLE', 
'DODGE', 'STRATTON', 'ROSAS', 'CHOI', 'TRIPP', 'LEDBETTER', 'LAY', 'HIGHTOWER', 'HAYWOOD', 
'FELDMAN', 'EPPS', 'YEAGER', 'POSEY', 'SYLVESTER', 'SCRUGGS', 'COPE', 'STUBBS', 'RICHEY', 
'OVERTON', 'TROTTER', 'SPRAGUE', 'CORDERO', 'BUTCHER', 'BURGER', 'STILES', 'BURGOS', 'WOODSON', 
'HORNER', 'BASSETT', 'PURCELL', 'HASKINS', 'GEE', 'AKINS', 'ABRAHAM', 'HOYT', 'ZIEGLER', 
'SPAULDING', 'HADLEY', 'GRUBBS', 'SUMNER', 'MURILLO', 'ZAVALA', 'SHOOK', 'LOCKWOOD', 'JARRETT', 
'DRISCOLL', 'DAHL', 'THORPE', 'SHERIDAN', 'REDMOND', 'PUTNAM', 'MCWILLIAMS', 'MCRAE', 'CORNELL', 
'FELTON', 'ROMANO', 'JOINER', 'SADLER', 'HEDRICK', 'HAGER', 'HAGEN', 'FITCH', 'COULTER', 
'THACKER', 'MANSFIELD', 'LANGSTON', 'GUIDRY', 'FERREIRA', 'CORLEY', 'CONN', 'ROSSI', 'LACKEY', 
'CODY', 'BAEZ', 'SAENZ', 'MCNAMARA', 'DARNELL', 'MICHEL', 'MCMULLEN', 'MCKENNA', 'MCDONOUGH', 
'LINK', 'ENGEL', 'BROWNE', 'ROPER', 'PEACOCK', 'EUBANKS', 'DRUMMOND', 'STRINGER', 'PRITCHETT', 
'PARHAM', 'MIMS', 'LANDERS', 'HAM', 'GRAYSON', 'STACY', 'SCHAFER', 'EGAN', 'TIMMONS', 
'OHARA', 'KEEN', 'HAMLIN', 'FINN', 'CORTES', 'MCNAIR', 'LOUIS', 'CLIFFORD', 'NADEAU', 
'MOSELEY', 'MICHAUD', 'ROSEN', 'OAKES', 'KURTZ', 'JEFFERS', 'CALLOWAY', 'BEAL', 'BAUTISTA', 
'WINN', 'SUGGS', 'STERN', 'STAPLETON', 'LYLES', 'LAIRD', 'MONTANO', 'DIAMOND', 'DAWKINS', 
'ROLAND', 'HAGAN', 'GOLDMAN', 'BRYSON', 'BARAJAS', 'LOVETT', 'SEGURA', 'METZ', 'LOCKETT', 
'LANGFORD', 'HINSON', 'EASTMAN', 'ROCK', 'HOOKS', 'WOODY', 'SMALLWOOD', 'SHAPIRO', 'CROWELL', 
'WHALEN', 'TRIPLETT', 'HOOKER', 'CHATMAN', 'ALDRICH', 'CAHILL', 'YOUNGBLOOD', 'YBARRA', 'STALLINGS', 
'SHEETS', 'SAMUEL', 'REEDER', 'PERSON', 'PACK', 'LACEY', 'CONNELLY', 'BATEMAN', 'ABERNATHY', 
'WINKLER', 'WILKES', 'MASTERS', 'HACKETT', 'GRANGER', 'GILLIS', 'SCHMITZ', 'SAPP', 'NAPIER', 
'SOUZA', 'LANIER', 'GOMES', 'WEIR', 'OTERO', 'LEDFORD', 'BURROUGHS', 'BABCOCK', 'VENTURA', 
'SIEGEL', 'DUGAN', 'CLINTON', 'CHRISTIE', 'BLEDSOE', 'ATWOOD', 'WRAY', 'VARNER', 'SPANGLER', 
'OTTO', 'ANAYA', 'STALEY', 'KRAFT', 'FOURNIER', 'EDDY', 'BELANGER', 'WOLFF', 'THORNE', 
'BYNUM', 'BURNETTE', 'BOYKIN', 'SWENSON', 'PURVIS', 'PINA', 'KHAN', 'DUVALL', 'DARBY', 
'XIONG', 'KAUFFMAN', 'ALI', 'YU', 'HEALY', 'ENGLE', 'CORONA', 'BENOIT', 'VALLE', 
'STEINER', 'SPICER', 'SHAVER', 'RANDLE', 'LUNDY', 'DOW', 'CHIN', 'CALVERT', 'STATON', 
'NEFF', 'KEARNEY', 'DARDEN', 'OAKLEY', 'MEDEIROS', 'MCCRACKEN', 'CRENSHAW', 'BLOCK', 'BEAVER', 
'PERDUE', 'DILL', 'WHITTAKER', 'TOBIN', 'CORNELIUS', 'WASHBURN', 'HOGUE', 'GOODRICH', 'EASLEY', 
'BRAVO', 'DENNISON', 'VERA', 'SHIPLEY', 'KERNS', 'JORGENSEN', 'CRAIN', 'ABEL', 'VILLALOBOS', 
'MAURER', 'LONGORIA', 'KEENE', 'COON', 'SIERRA', 'WITHERSPOON', 'STAPLES', 'PETTIT', 'KINCAID', 
'EASON', 'MADRID', 'ECHOLS', 'LUSK', 'WU', 'STAHL', 'CURRIE', 'THAYER', 'SHULTZ', 
'SHERWOOD', 'MCNALLY', 'SEAY', 'NORTH', 'MAHER', 'KENNY', 'HOPE', 'GAGNE', 'BARROW', 
'NAVA', 'MYLES', 'MORELAND', 'HONEYCUTT', 'HEARN', 'DIGGS', 'CARON', 'WHITTEN', 'WESTBROOK', 
'STOVALL', 'RAGLAND', 'QUEEN', 'MUNSON', 'MEIER', 'LOONEY', 'KIMBLE', 'JOLLY', 'HOBSON', 
'LONDON', 'GODDARD', 'CULVER', 'BURR', 'PRESLEY', 'NEGRON', 'CONNELL', 'TOVAR', 'MARCUS', 
'HUDDLESTON', 'HAMMER', 'ASHBY', 'SALTER', 'ROOT', 'PENDLETON', 'OLEARY', 'NICKERSON', 'MYRICK', 
'JUDD', 'JACOBSEN', 'ELLIOT', 'BAIN', 'ADAIR', 'STARNES', 'SHELDON', 'MATOS', 'LIGHT', 
'BUSBY', 'HERNDON', 'HANLEY', 'BELLAMY', 'JACK', 'DOTY', 'BARTLEY', 'YAZZIE', 'ROWELL', 
'PARSON', 'GIFFORD', 'CULLEN', 'CHRISTIANSEN', 'BENAVIDES', 'BARNHART', 'TALBOT', 'MOCK', 'CRANDALL', 
'CONNORS', 'BONDS', 'WHITT', 'GAGE', 'BERGMAN', 'ARREDONDO', 'ADDISON', 'MARION', 'LUJAN', 
'DOWDY', 'JERNIGAN', 'HUYNH', 'BOUCHARD', 'DUTTON', 'RHOADES', 'OUELLETTE', 'KISER', 'RUBIN', 
'HERRINGTON', 'HARE', 'DENNY', 'BLACKMAN', 'BABB', 'ALLRED', 'RUDD', 'PAULSON', 'OGDEN', 
'KOENIG', 'JACOB', 'IRVING', 'GEIGER', 'BEGAY', 'PARRA', 'CHAMPION', 'LASSITER', 'HAWK', 
'ESPOSITO', 'CHO', 'WALDRON', 'VERNON', 'RANSOM', 'PRATHER', 'KEENAN', 'JEAN', 'GROVER', 
'CHACON', 'VICK', 'SANDS', 'ROARK', 'PARR', 'MAYBERRY', 'GREENBERG', 'COLEY', 'BRUNER', 
'WHITMAN', 'SKAGGS', 'SHIPMAN', 'MEANS', 'LEARY', 'HUTTON', 'ROMO', 'MEDRANO', 'LADD', 
'KRUSE', 'FRIEND', 'DARLING', 'ASKEW', 'VALENTIN', 'SCHULZ', 'ALFARO', 'TABOR', 'MOHR', 
'GALLO', 'BERMUDEZ', 'PEREIRA', 'ISAAC', 'BLISS', 'REAVES', 'FLINT', 'COMER', 'BOSTON', 
'WOODALL', 'NAQUIN', 'GUEVARA', 'EARL', 'DELONG', 'CARRIER', 'PICKENS', 'BRAND', 'TILLEY', 
'SCHAFFER', 'READ', 'LIM', 'KNUTSON', 'FENTON', 'DORAN', 'CHU', 'VOGT', 'VANN', 
'PRESCOTT', 'MCLAIN', 'LANDIS', 'CORCORAN', 'AMBROSE', 'ZAPATA', 'HYATT', 'HEMPHILL', 'FAULK', 
'CALL', 'DOVE', 'BOUDREAUX', 'ARAGON', 'WHITLOCK', 'TREJO', 'TACKETT', 'SHEARER', 'SALDANA', 
'HANKS', 'GOLD', 'DRIVER', 'MCKINNON', 'KOEHLER', 'CHAMPAGNE', 'BOURGEOIS', 'POOL', 'KEYES', 
'GOODSON', 'FOOTE', 'EARLY', 'LUNSFORD', 'GOLDSMITH', 'FLOOD', 'WINSLOW', 'SAMS', 'REAGAN', 
];

var PERSON_NAME_SUFFIX = [
    "", "", "", "", "", "", "", "", "", "I", "II", "III"
];

function randPersonName() {
    var result = [];
//    appendRandName(result, PERSON_NAME_PREFIX);
    appendRandName(result, PERSON_LAST_NAME);
    appendRandName(result, PERSON_FIRST_NAME);
//    appendRandName(result, PERSON_NAME_SUFFIX);
    return result.join(" ");
}

var BOOK_NAME_PART = [
'the', 'of', 'and', 'to', 'a', 'in', 'is', 'you', 'are', 
'for', 'that', 'or', 'it', 'as', 'be', 'on', 'your', 'with', 
'can', 'have', 'this', 'an', 'by', 'not', 'but', 'at', 'from', 
'I', 'they', 'more', 'will', 'if', 'some', 'there', 'what', 'about', 
'which', 'when', 'one', 'their', 'all', 'also', 'how', 'many', 'do', 
'has', 'most', 'people', 'other', 'time', 'so', 'was', 'we', 'these', 
'may', 'like', 'use', 'into', 'than', 'up', 'out', 'who', 'them', 
'make', 'because', 'such', 'through', 'get', 'work', 'even', 'different', 'its', 
'no', 'our', 'new', 'film', 'just', 'only', 'see', 'used', 'good', 
'water', 'been', 'need', 'should', 'very', 'any', 'history', 'often', 'way', 
'well', 'art', 'know', 'were', 'then', 'my', 'first', 'would', 'money', 
'each', 'over', 'world', 'information', 'map', 'find', 'where', 'much', 'take', 
'two', 'want', 'important', 'family', 'those', 'example', 'while', 'he', 'look', 
'government', 'before', 'help', 'between', 'go', 'own', 'however', 'business', 'us', 
'great', 'his', 'being', 'another', 'health', 'same', 'study', 'why', 'few', 
'game', 'might', 'think', 'free', 'too', 'had', 'hi', 'right', 'still', 
'system', 'after', 'computer', 'best', 'must', 'her', 'life', 'since', 'could', 
'does', 'now', 'during', 'learn', 'around', 'usually', 'form', 'meat', 'air', 
'day', 'place', 'become', 'number', 'public', 'read', 'keep', 'part', 'start', 
'year', 'every', 'field', 'large', 'once', 'available', 'down', 'give', 'fish', 
'human', 'both', 'local', 'sure', 'something', 'without', 'come', 'me', 'back', 
'better', 'general', 'process', 'she', 'heat', 'thanks', 'specific', 'enough', 'long', 
'lot', 'hand', 'popular', 'small', 'though', 'experience', 'include', 'job', 'music', 
'person', 'really', 'although', 'thank', 'book', 'early', 'reading', 'end', 'method', 
'never', 'less', 'play', 'able', 'data', 'feel', 'high', 'off', 'point', 
'type', 'whether', 'food', 'understanding', 'here', 'home', 'certain', 'economy', 'little', 
'theory', 'tonight', 'law', 'put', 'under', 'value', 'always', 'body', 'common', 
'market', 'set', 'bird', 'guide', 'provide', 'change', 'interest', 'literature', 'sometimes', 
'problem', 'say', 'next', 'create', 'simple', 'software', 'state', 'together', 'control', 
'knowledge', 'power', 'radio', 'ability', 'basic', 'course', 'economics', 'hard', 'add', 
'company', 'known', 'love', 'past', 'price', 'size', 'away', 'big', 'internet', 
'possible', 'television', 'three', 'understand', 'various', 'yourself', 'card', 'difficult', 'including', 
'list', 'mind', 'particular', 'real', 'science', 'trade', 'consider', 'either', 'library', 
'likely', 'nature', 'fact', 'line', 'product', 'care', 'group', 'idea', 'risk', 
'several', 'someone', 'temperature', 'united', 'word', 'fat', 'force', 'key', 'light', 
'simply', 'today', 'training', 'until', 'major', 'name', 'personal', 'school', 'top', 
'current', 'generally', 'historical', 'investment', 'left', 'national', 'amount', 'level', 'order', 
'practice', 'research', 'sense', 'service', 'area', 'cut', 'hot', 'instead', 'least', 
'natural', 'physical', 'piece', 'show', 'society', 'try', 'check', 'choose', 'develop', 
'second', 'useful', 'web', 'activity', 'boss', 'short', 'story', 'call', 'industry', 
'last', 'media', 'mental', 'move', 'pay', 'sport', 'thing', 'actually', 'against', 
'far', 'fun', 'house', 'let', 'page', 'remember', 'term', 'test', 'within', 
'along', 'answer', 'increase', 'oven', 'quite', 'scared', 'single', 'sound', 'again', 
'community', 'definition', 'focus', 'individual', 'matter', 'safety', 'turn', 'everything', 'kind', 
'quality', 'soil', 'ask', 'board', 'buy', 'development', 'guard', 'hold', 'language', 
'later', 'main', 'offer', 'oil', 'picture', 'potential', 'professional', 'rather', 'access', 
'additional', 'almost', 'especially', 'garden', 'international', 'lower', 'management', 'open', 'player', 
'range', 'rate', 'reason', 'travel', 'variety', 'video', 'week', 'above', 'according', 
'cook', 'determine', 'future', 'site', 'alternative', 'demand', 'ever', 'exercise', 'following', 
'image', 'quickly', 'special', 'working', 'case', 'cause', 'coast', 'probably', 'security', 
'true', 'whole', 'action', 'age', 'among', 'bad', 'boat', 'country', 'dance', 
'exam', 'excuse', 'grow', 'movie', 'organization', 'record', 'result', 'section', 'across', 
'already', 'below', 'building', 'mouse', 'allow', 'cash', 'class', 'clear', 'dry', 
'easy', 'emotional', 'equipment', 'live', 'nothing', 'period', 'physics', 'plan', 'store', 
'tax', 'analysis', 'cold', 'commercial', 'directly', 'full', 'involved', 'itself', 'low', 
'old', 'policy', 'political', 'purchase', 'series', 'side', 'subject', 'supply', 'therefore', 
'thought', 'basis', 'boyfriend', 'deal', 'direction', 'mean', 'primary', 'space', 'strategy', 
'technology', 'worth', 'army', 'camera', 'fall', 'freedom', 'paper', 'rule', 'similar', 
'stock', 'weather', 'yet', 'bring', 'chance', 'environment', 'everyone', 'figure', 'improve', 
'man', 'model', 'necessary', 'positive', 'produce', 'search', 'source', 'beginning', 'child', 
'earth', 'else', 'healthy', 'instance', 'maintain', 'month', 'present', 'program', 'spend', 
'talk', 'truth', 'upset', 'begin', 'chicken', 'close', 'creative', 'design', 'feature', 
'financial', 'head', 'marketing', 'material', 'medical', 'purpose', 'question', 'rock', 'salt', 
'tell', 'themselves', 'traditional', 'university', 'writing', 'act', 'article', 'birth', 'car', 
'cost', 'department', 'difference', 'dog', 'drive', 'exist', 'federal', 'goal', 'green', 
'late', 'news', 'object', 'scale', 'sun', 'support', 'tend', 'thus', 'audience', 
'enjoy', 'entire', 'fishing', 'fit', 'glad', 'growth', 'income', 'marriage', 'note', 
'perform', 'profit', 'proper', 'related', 'remove', 'rent', 'return', 'run', 'speed', 
'strong', 'style', 'throughout', 'user', 'war', 'actual', 'appropriate', 'bank', 'combination', 
'complex', 'content', 'craft', 'due', 'easily', 'effective', 'eventually', 'exactly', 'failure', 
'half', 'inside', 'meaning', 'medicine', 'middle', 'outside', 'philosophy', 'regular', 'reserve', 
'standard', 'bus', 'decide', 'exchange', 'eye', 'fast', 'fire', 'identify', 'independent', 
'leave', 'original', 'position', 'pressure', 'reach', 'rest', 'serve', 'stress', 'teacher', 
'watch', 'wide', 'advantage', 'beautiful', 'benefit', 'box', 'charge', 'communication', 'complete', 
'continue', 'frame', 'issue', 'limited', 'night', 'protect', 'require', 'significant', 'step', 
'successful', 'unless', 'active', 'break', 'chemistry', 'cycle', 'disease', 'disk', 'electrical', 
'energy', 'expensive', 'face', 'interested', 'item', 'metal', 'nation', 'negative', 'occur', 
'paint', 'pregnant', 'review', 'road', 'role', 'room', 'safe', 'screen', 'soup', 
'stay', 'structure', 'view', 'visit', 'visual', 'write', 'wrong', 'account', 'advertising', 
'affect', 'ago', 'anyone', 'approach', 'avoid', 'ball', 'behind', 'certainly', 'concerned', 
'cover', 'discipline', 'location', 'medium', 'normally', 'prepare', 'quick', 'ready', 'report', 
'rise', 'share', 'success', 'addition', 'apartment', 'balance', 'bit', 'black', 'bottom', 
'build', 'choice', 'education', 'gift', 'impact', 'machine', 'math', 'moment', 'painting', 
'politics', 'shape', 'straight', 'tool', 'walk', 'white', 'wind', 'achieve', 'address', 
'attention', 'average', 'believe', 'beyond', 'career', 'culture', 'decision', 'direct', 'event', 
'excellent', 'extra', 'intelligent', 'interesting', 'junior', 'morning', 'pick', 'poor', 'pot', 
'pretty', 'property', 'receive', 'seem', 'shopping', 'sign', 'student', 'table', 'task', 
'unique', 'wood', 'anything', 'classic', 'competition', 'condition', 'contact', 'credit', 'currently', 
'discuss', 'distribution', 'egg', 'entertainment', 'final', 'happy', 'hope', 'ice', 'lift', 
'mix', 'network', 'north', 'office', 'overall', 'population', 'president', 'private', 'realize', 
'responsible', 'separate', 'square', 'stop', 'teach', 'unit', 'western', 'yes', 'alone', 
'attempt', 'category', 'cigarette', 'concern', 'contain', 'context', 'cute', 'date', 'effect', 
'extremely', 'familiar', 'finally', 'fly', 'follow', 'helpful', 'introduction', 'link', 'official', 
'opportunity', 'perfect', 'performance', 'post', 'recent', 'refer', 'solve', 'star', 'voice', 
'willing', 'born', 'bright', 'broad', 'capital', 'challenge', 'comfortable', 'constantly', 'describe', 
'despite', 'driver', 'flat', 'flight', 'friend', 'gain', 'him', 'length', 'magazine', 
'maybe', 'newspaper', 'nice', 'prefer', 'prevent', 'properly', 'relationship', 'rich', 'save', 
'self', 'shot', 'soon', 'specifically', 'stand', 'teaching', 'warm', 'wonderful', 'young', 
'ahead', 'brush', 'cell', 'couple', 'daily', 'dealer', 'debate', 'discover', 'ensure', 
'exit', 'expect', 'experienced', 'fail', 'finding', 'front', 'function', 'heavy', 'hello', 
'highly', 'immediately', 'impossible', 'invest', 'lack', 'lake', 'lead', 'listen', 'living', 
'member', 'message', 'phone', 'plant', 'plastic', 'reduce', 'relatively', 'scene', 'serious', 
'slowly', 'speak', 'spot', 'summer', 'taste', 'theme', 'towards', 'track', 'valuable', 
'whatever', 'wing', 'worry', 'appear', 'appearance', 'association', 'brain', 'button', 'click', 
'concept', 'correct', 'customer', 'death', 'desire', 'discussion', 'explain', 'explore', 'express', 
'fairly', 'fixed', 'foot', 'gas', 'handle', 'housing', 'huge', 'inflation', 'influence', 
'insurance', 'involve', 'leading', 'lose', 'meet', 'mood', 'notice', 'primarily', 'rain', 
'rare', 'release', 'sell', 'slow', 'technical', 'typical', 'upon', 'wall', 'woman', 
'advice', 'afford', 'agree', 'base', 'blood', 'clean', 'competitive', 'completely', 'critical', 
'damage', 'distance', 'effort', 'electronic', 'expression', 'feeling', 'finish', 'fresh', 'hear', 
'immediate', 'importance', 'normal', 'opinion', 'otherwise', 'pair', 'payment', 'plus', 'press', 
'reality', 'remain', 'represent', 'responsibility', 'ride', 'savings', 'secret', 'situation', 'skill', 
'spread', 'spring', 'staff', 'statement', 'sugar', 'target', 'text', 'tough', 'ultimately', 
'wait', 'wealth', 'whenever', 'whose', 'widely', 'animal', 'application', 'apply', 'author', 
'aware', 'brown', 'budget', 'cheap', 'city', 'complicated', 'county', 'deep', 'depth', 
'discount', 'display', 'educational', 'environmental', 'estate', 'file', 'flow', 'forget', 'foundation', 
'global', 'grandmother', 'ground', 'heart', 'hit', 'legal', 'lesson', 'minute', 'near', 
'objective', 'officer', 'perspective', 'phase', 'photo', 'recently', 'recipe', 'recommend', 'reference', 
'register', 'relevant', 'rely', 'secure', 'seriously', 'shoot', 'sky', 'stage', 'stick', 
'studio', 'thin', 'title', 'topic', 'touch', 'trouble', 'vary', 'accurate', 'advanced', 
'bowl', 'bridge', 'campaign', 'cancel', 'capable', 'character', 'chemical', 'club', 'collection', 
'cool', 'cry', 'dangerous', 'depression', 'dump', 'edge', 'evidence', 'extreme', 'fan', 
'frequently', 'fully', 'generate', 'imagination', 'letter', 'lock', 'maximum', 'mostly', 'myself', 
'naturally', 'nearly', 'novel', 'obtain', 'occasionally', 'option', 'organized', 'pack', 'park', 
'passion', 'percentage', 'plenty', 'push', 'quarter', 'resource', 'select', 'setting', 'skin', 
'sort', 'weight', 'accept', 'ad', 'agency', 'baby', 'background', 'carefully', 'carry', 
'clearly', 'college', 'communicate', 'complain', 'conflict', 'connection', 'criticism', 'debt', 'depend', 
'description', 'die', 'dish', 'dramatic', 'eat', 'efficient', 'enter', 'essentially', 'exact', 
'factor', 'fair', 'fill', 'fine', 'formal', 'forward', 'fruit', 'glass', 'happen', 
'indicate', 'joint', 'jump', 'kick', 'master', 'memory', 'muscle', 'opposite', 'pass', 
'patience', 'pitch', 'possibly', 'powerful', 'red', 'remote', 'secretary', 'slightly', 'solution', 
'somewhat', 'strength', 'suggest', 'survive', 'total', 'traffic', 'treat', 'trip', 'vast', 
'vegetable', 'abuse', 'administration', 'appeal', 'appreciate', 'aspect', 'attitude', 'beat', 'burn', 
'chart', 'compare', 'deposit', 'director', 'equally', 'foreign', 'gear', 'greatly', 'hungry', 
'ideal', 'imagine', 'kitchen', 'land', 'log', 'lost', 'manage', 'mother', 'necessarily', 
'net', 'party', 'personality', 'personally', 'practical', 'principle', 'print', 'psychological', 'psychology', 
'raise', 'rarely', 'recommendation', 'regularly', 'relative', 'response', 'sale', 'season', 'selection', 
'severe', 'signal', 'similarly', 'sleep', 'smooth', 'somewhere', 'spirit', 'storage', 'street', 
'suitable', 'tree', 'version', 'wave', 'advance', 'alcohol', 'anywhere', 'argument', 'basically', 
'belt', 'bench', 'closed', 'closely', 'commission', 'complaint', 'connect', 'consist', 'contract', 
'contribute', 'copy', 'dark', 'differ', 'double', 'draw', 'drop', 'effectively', 'emphasis', 
'encourage', 'equal', 'everybody', 'expand', 'firm', 'fix', 'frequent', 'highway', 'hire', 
'initially', 'internal', 'join', 'kill', 'literally', 'loss', 'mainly', 'membership', 'merely', 
'minimum', 'numerous', 'path', 'possession', 'preparation', 'progress', 'project', 'prove', 'react', 
'recognize', 'relax', 'replace', 'sea', 'sensitive', 'sit', 'south', 'status', 'steak', 
'stuff', 'sufficient', 'tap', 'ticket', 'tour', 'union', 'unusual', 'win', 'agreement', 
'angle', 'attack', 'blue', 'borrow', 'breakfast', 'cancer', 'claim', 'confidence', 'consistent', 
'constant', 'cultural', 'currency', 'daughter', 'degree', 'doctor', 'dot', 'drag', 'dream', 
'drink', 'duty', 'earn', 'emphasize', 'employment', 'enable', 'engineering', 'entry', 'essay', 
'existing', 'famous', 'father', 'fee', 'finance', 'gently', 'guess', 'hopefully', 'hour', 
'interaction', 'juice', 'limit', 'luck', 'milk', 'minor', 'mixed', 'mixture', 'mouth', 
'nor', 'operate', 'originally', 'peace', 'pipe', 'please', 'preference', 'previous', 'pull', 
'pure', 'raw', 'reflect', 'region', 'republic', 'roughly', 'seat', 'send', 'significantly', 
'soft', 'solid', 'stable', 'storm', 'substance', 'team', 'tradition', 'trick', 'virus', 
'wear', 'weird', 'wonder', 'actor', 'afraid', 'afternoon', 'amazing', 'annual', 'anticipate', 
'assume', 'bat', 'beach', 'blank', 'busy', 'catch', 'chain', 'classroom', 'consideration', 
'count', 'cream', 'crew', 'dead', 'delivery', 'detail', 'detailed', 'device', 'difficulty', 
'doubt', 'drama', 'election', 'engage', 'engine', 'enhance', 'examine', 'false', 'feed', 
'football', 'forever', 'gold', 'guidance', 'hotel', 'impress', 'install', 'interview', 'kid', 
'mark', 'match', 'mission', 'nobody', 'obvious', 'ourselves', 'owner', 'pain', 'participate', 
'pleasure', 'priority', 'protection', 'repeat', 'round', 'score', 'screw', 'seek', 'sex', 
'sharp', 'shop', 'shower', 'sing', 'slide', 'strip', 'suggestion', 'suit', 'tension', 
'thick', 'tone', 'totally', 'twice', 'variation', 'whereas', 'window', 'wise', 'wish', 
'agent', 'anxiety', 'atmosphere', 'awareness', 'band', 'bath', 'block', 'bone', 'bread', 
'calendar', 'candidate', 'cap', 'careful', 'climate', 'coat', 'collect', 'combine', 'command', 
'comparison', 'confusion', 'construction', 'contest', 'corner', 'court', 'cup', 'dig', 'district', 
'divide', 'door', 'east', 'elevator', 'elsewhere', 'emotion', 'employee', 'employer', 'equivalent', 
'everywhere', 'except', 'finger', 'garage', 'guarantee', 'guest', 'hang', 'height', 'himself', 
'hole', 'hook', 'hunt', 'implement', 'initial', 'intend', 'introduce', 'latter', 'layer', 
'leadership', 'lecture', 'lie', 'mall', 'manager', 'manner', 'march', 'married', 'meeting', 
'mention', 'narrow', 'nearby', 'neither', 'nose', 'obviously', 'operation', 'parking', 'partner', 
'perfectly', 'physically', 'profile', 'proud', 'recording', 'relate', 'respect', 'rice', 'routine', 
'sample', 'schedule', 'settle', 'smell', 'somehow', 'spiritual', 'survey', 'swimming', 'telephone', 
'tie', 'tip', 'transportation', 'unhappy', 'wild', 'winter', 'absolutely', 'acceptable', 'adult', 
'aggressive', 'airline', 'apart', 'assure', 'attract', 'bag', 'battle', 'bed', 'bill', 
'boring', 'bother', 'brief', 'cake', 'charity', 'code', 'cousin', 'crazy', 'curve', 
'designer', 'dimension', 'disaster', 'distinct', 'distribute', 'dress', 'ease', 'eastern', 'editor', 
'efficiency', 'emergency', 'escape', 'evening', 'excitement', 'expose', 'extension', 'extent', 'farm', 
'feedback', 'fight', 'gap', 'gather', 'grade', 'guitar', 'hate', 'holiday', 'homework', 
'horror', 'horse', 'host', 'husband', 'leader', 'loan', 'logical', 'mistake', 'mom', 
'mountain', 'nail', 'noise', 'none', 'occasion', 'outcome', 'overcome', 'owe', 'package', 
'patient', 'pause', 'permission', 'phrase', 'presentation', 'prior', 'promotion', 'proof', 'race', 
'reasonable', 'reflection', 'refrigerator', 'relief', 'repair', 'resolution', 'revenue', 'rough', 'sad', 
'sand', 'scratch', 'sentence', 'session', 'shoulder', 'sick', 'singer', 'smoke', 'stomach', 
'strange', 'strict', 'strike', 'string', 'succeed', 'successfully', 'suddenly', 'suffer', 'surprised', 
'tennis', 'throw', 'tourist', 'towel', 'truly', 'vacation', 'virtually', 'west', 'wheel', 
'wine', 'acquire', 'adapt', 'adjust', 'administrative', 'altogether', 'anyway', 'argue', 'arise', 
'arm', 'aside', 'associate', 'automatic', 'automatically', 'basket', 'bet', 'blow', 'bonus', 
'border', 'branch', 'breast', 'brother', 'buddy', 'bunch', 'cabinet', 'childhood', 'chip', 
'church', 'civil', 'clothes', 'coach', 'coffee', 'confirm', 'cross', 'deeply', 'definitely', 
'deliberately', 'dinner', 'document', 'draft', 'drawing', 'dust', 'employ', 'encouraging', 'expert', 
'external', 'floor', 'former', 'god', 'golf', 'habit', 'hair', 'hardly', 'hearing', 
'hurt', 'illegal', 'incorporate', 'initiative', 'iron', 'judge', 'judgment', 'justify', 'knife', 
'lab', 'landscape', 'laugh', 'lay', 'league', 'loud', 'mail', 'massive', 'measurement', 
'mess', 'mobile', 'mode', 'mud', 'nasty', 'native', 'opening', 'orange', 'ordinary', 
'organize', 'ought', 'parent', 'pattern', 'pin', 'poetry', 'police', 'pool', 'possess', 
'possibility', 'pound', 'procedure', 'queen', 'ratio', 'readily', 'relation', 'relieve', 'request', 
'respond', 'restaurant', 'retain', 'royal', 'salary', 'satisfaction', 'sector', 'senior', 'shame', 
'shelter', 'shoe', 'shut', 'signature', 'significance', 'silver', 'somebody', 'song', 'southern', 
'split', 'strain', 'struggle', 'super', 'swim', 'tackle', 'tank', 'terribly', 'tight', 
'tooth', 'town', 'train', 'trust', 'unfair', 'unfortunately', 'upper', 'vehicle', 'visible', 
'volume', 'wash', 'waste', 'wife', 'yellow', 'yours', 'accident', 'airport', 'alive', 
'angry', 'appointment', 'arrival', 'assist', 'assumption', 'bake', 'bar', 'baseball', 'bell', 
'bike', 'blame', 'boy', 'brick', 'calculate', 'chair', 'chapter', 'closet', 'clue', 
'collar', 'comment', 'committee', 'compete', 'concerning', 'conference', 'consult', 'conversation', 'convert', 
'crash', 'database', 'deliver', 'dependent', 'desperate', 'devil', 'diet', 'enthusiasm', 'error', 
'exciting', 'explanation', 'extend', 'farmer', 'fear', 'fold', 'forth', 'friendly', 'fuel', 
'funny', 'gate', 'girl', 'glove', 'grab', 'gross', 'hall', 'herself', 'hide', 
'historian', 'hospital', 'ill', 'injury', 'instruction', 'investigate', 'jacket', 'lucky', 'lunch', 
'maintenance', 'manufacturer', 'meal', 'miss', 'monitor', 'mortgage', 'negotiate', 'nurse', 'pace', 
'panic', 'peak', 'perception', 'permit', 'pie', 'plane', 'poem', 'presence', 'proposal', 
'provided', 'qualify', 'quote', 'realistic', 'reception', 'recover', 'replacement', 'resolve', 'retire', 
'revolution', 'reward', 'rid', 'river', 'roll', 'row', 'sandwich', 'shock', 'sink', 
'slip', 'son', 'sorry', 'spare', 'speech', 'spite', 'spray', 'surprise', 'suspect', 
'sweet', 'swing', 'tea', 'till', 'transition', 'twist', 'ugly', 'unlikely', 'upstairs', 
'usual', 'village', 'warning', 'weekend', 'weigh', 'welcome', 'winner', 'worker', 'writer', 
'yard', 'abroad', 'alarm', 'anxious', 'arrive', 'assistance', 'attach', 'behave', 'bend', 
'bicycle', 'bite', 'blind', 'bottle', 'brave', 'breath', 'briefly', 'buyer', 'cable', 
'calm', 'candle', 'celebrate', 'chest', 'chocolate', 'clerk', 'cloud', 'comprehensive', 'concentrate', 
'concert', 'conclusion', 'contribution', 'convince', 'cookie', 'counter', 'courage', 'curious', 'dad', 
'desk', 'dirty', 'disagree', 'downtown', 'drawer', 'establish', 'establishment', 'estimate', 'examination', 
'flower', 'garbage', 'grand', 'grandfather', 'grocery', 'harm', 'honest', 'honey', 'ignore', 
'imply', 'impression', 'impressive', 'improvement', 'independence', 'informal', 'inner', 'insect', 'insist', 
'inspection', 'inspector', 'king', 'knee', 'ladder', 'lawyer', 'leather', 'load', 'loose', 
'male', 'menu', 'mine', 'mirror', 'moreover', 'neck', 'penalty', 'pension', 'piano', 
'plate', 'pleasant', 'pleased', 'potato', 'profession', 'professor', 'prompt', 'proposed', 'purple', 
'pursue', 'quantity', 'quiet', 'reaction', 'refuse', 'regret', 'remaining', 'requirement', 'reveal', 
'ruin', 'rush', 'salad', 'sexual', 'shake', 'shift', 'shine', 'ship', 'sister', 
'skirt', 'slice', 'snow', 'specialist', 'specify', 'steal', 'stroke', 'strongly', 'suck', 
'sudden', 'supermarket', 'surround', 'switch', 'terrible', 'tired', 'tongue', 'trash', 'tune', 
'unable', 'warn', 'weak', 'weakness', 'wedding', 'wooden', 'worried', 'yeah', 'zone', 
'accuse', 'admire', 'admit', 'adopt', 'affair', 'ambition', 'analyst', 'anger', 'announce', 
'anybody', 'apologize', 'apple', 'approve', 'asleep', 'assignment', 'assistant', 'attend', 'award', 
'bathroom', 'bear', 'bedroom', 'beer', 'belong', 'bid', 'birthday', 'bitter', 'boot', 
'brilliant', 'bug', 'camp', 'candy', 'carpet', 'cat', 'celebration', 'champion', 'championship', 
'channel', 'cheek', 'client', 'clock', 'comfort', 'commit', 'confident', 'conscious', 'consequence', 
'cow', 'crack', 'criticize', 'dare', 'dear', 'decent', 'delay', 'departure', 'deserve', 
'destroy', 'diamond', 'dirt', 'disappointed', 'drunk', 'ear', 'embarrassed', 'empty', 'engineer', 
'entrance', 'fault', 'female', 'fortune', 'friendship', 'funeral', 'gene', 'girlfriend', 'grass', 
'guilty', 'guy', 'hat', 'hell', 'hesitate', 'highlight', 'honestly', 'hurry', 'illustrate', 
'incident', 'indication', 'inevitable', 'inform', 'intention', 'invite', 'island', 'joke', 'jury', 
'kiss', 'lady', 'leg', 'lip', 'lonely', 'mad', 'manufacturing', 'marry', 'mate', 
'midnight', 'motor', 'neat', 'negotiation', 'nerve', 'nervous', 'nowhere', 'obligation', 'odd', 
'ok', 'passage', 'passenger', 'pen', 'persuade', 'pizza', 'platform', 'poet', 'pollution', 
'pop', 'pour', 'pray', 'pretend', 'previously', 'pride', 'priest', 'prize', 'promise', 
'propose', 'punch', 'quit', 'recognition', 'remarkable', 'remind', 'reply', 'representative', 'reputation', 
'resident', 'resist', 'resort', 'ring', 'rip', 'roof', 'rope', 'rub', 'sail', 
'scheme', 'script', 'shall', 'shirt', 'silly', 'sir', 'slight', 'smart', 'smile', 
'sock', 'speaker', 'spell', 'station', 'stranger', 'stretch', 'stupid', 'submit', 'substantial', 
'suppose', 'surgery', 'suspicious', 'sympathy', 'tale', 'tall', 'tear', 'temporary', 'throat', 
'tiny', 'toe', 'tomorrow', 'tower', 'trainer', 'translate', 'truck', 'uncle', 'wake', 
'weekly', 'whoever', 'witness', 'wrap', 'yesterday', 'youth', 
];

function randBookName(result) {
    var arr = result || [];
    appendRandName(arr, BOOK_NAME_PART, 1, 18);
    return result == null ? arr.join(" ") : result;
}

//
// Generation code
//

function sqlStringify(str) {
    if (str == null) {
        return 'null';
    }
    if (str.indexOf("'") < 0) {
        return "'" + str + "'";
    }
    return "'" + str.split("'").join("''") + "'";
}


function nameValuePairs(initFn) {
    var values = {};
    var ins = function (id, name) {
        if (name in values) {
            throw new Error("A value with name=" + name + " has been inserted, existing id=" + values[name]);
        }
        values[name] = id;
    }
    initFn(ins);
    return values;
}


function setUpEntityTypes(context) {
    context.entityTypes = nameValuePairs(function (ins) {
        ins(1, 'author');
        ins(2, 'language');
        ins(3, 'person');
        ins(5, 'book');
        ins(6, 'movie');
        ins(7, 'series');
        ins(8, 'genre');
        ins(9, 'book_origin');
    });
}

function setUpLanguages(context) {
    context.languages = nameValuePairs(function (ins) {
        ins(50, "en");
        ins(51, "ru");
        ins(52, "cn");
    });
}

function setUpGenres(context) {
    context.genres = nameValuePairs(function (ins) {
        ins(101, 'Poetry');
        ins(102, 'Fantasy');
        ins(103, 'Science Fiction');
        ins(105, 'Biography');
        ins(106, 'Novel');
        ins(107, 'Drama');
        ins(114, 'Modern');
        ins(117, 'Classic');
        ins(118, 'History');
        ins(119, 'Adaptation');
        ins(123, 'Tale');
        ins(129, 'Short Story');
        ins(140, 'Realistic Fiction');
        ins(141, 'Folklore');
        ins(145, 'Fable');
        ins(148, 'Speech');
        ins(150, 'Narrative');
        ins(185, 'Essay');
        ins(186, 'Mystery');
    });
}

function insertOrigins(context) {
   var count = context.originsCount || 3;
    context.origins = nameValuePairs(function (ins) {
        for (var i = 0; i < count; ++i) {
            ins(200 + i, "Origin_" + i);
        }
    });
   
}

function insertSeries(context) {
    var count = context.seriesCount || 5;
    context.series = nameValuePairs(function (ins) {
        for (var i = 0; i < count; ++i) {
            ins(300 + i, "Series_" + i);
        }
    });
}

function idByEntityTypeName(context, name) {
    if (name in context.entityTypes) {
        return context.entityTypes[name];
    }

    throw new Error("No entity type with name=" + name);
}

function getUniqueName(entityMap, generatorFn) {
    var name;
    for (;;) {
        name = generatorFn();
        if (name in entityMap) {
            continue;
        }
        return name;
    }
}

function insertAuthors(context) {
    var authors = {};
    var count = context.authorCount || 10;

    var next = 1000;
    for (var i = 0; i < count; ++i) {
        next = next + rand(1, 3);
        authors[getUniqueName(authors, randPersonName)] = next;
    }

    context.authors = authors;
}

function insertBooks(context) {
    var books = {};
    var count = context.bookCount || 15;

    var next = 1000000;
    for (var i = 0; i < count; ++i) {
        next = next + rand(1, 3);
        var title = randBookName();
        books[getUniqueName(books, randBookName)] = next;
    }

    context.books = books;
}

function insertBlock(context, block, comment, name, result) {
    comment(name + " entries");
    var typeId = idByEntityTypeName(context, name);
    Object.keys(block).map(function (name) {
        result.push("INSERT INTO item (id, name, type_id) VALUES (" + block[name] + ", " +
            sqlStringify(name) + ", " + typeId + ");");
    });
}

function generateContent(context, result) {
    var comment = function (what) {
        result.push("\n");
        result.push("-- " + what);
    }

    comment("Entity Types");
    Object.keys(context.entityTypes).map(function (name) {
        result.push("INSERT INTO entity_type (id, name) VALUES (" + context.entityTypes[name] + ", " +
            sqlStringify(name) + ");");
    });

    insertBlock(context, context.genres, comment, "genre", result);
    insertBlock(context, context.languages, comment, "language", result);
    insertBlock(context, context.series, comment, "series", result);
    insertBlock(context, context.origins, comment, "book_origin", result);
    insertBlock(context, context.authors, comment, "person", result);
    insertBlock(context, context.books, comment, "book", result);
}

function generateTestDb(context) {
    context = context || {};

    var result = [];

    setUpEntityTypes(context);
    setUpGenres(context);
    setUpLanguages(context);
    insertOrigins(context);
    insertSeries(context);
    insertAuthors(context);
    insertBooks(context);

    generateContent(context, result);

    if (context.fileName != null) {
        fs.writeFileSync(context.fileName, result.join("\n"));
    } else {
        console.log(result.join("\n"));
    }
}


