--------------------------
----------TASK 2----------
--------------------------
-- Start by creating a database
CREATE DATABASE TASKDWH

--Create a schema inside the database
USE TASKDWH
GO
CREATE SCHEMA [TASK]

--Create a table in the database to store the data
USE TASKDWH
GO
CREATE TABLE records (
kontakt_id int PRIMARY KEY,
klient_id int,
pracownik_id int,
status varchar(50),
kontakt_ts datetime
)
-- I just convert the json file online to csv file and copy paste the data

--The query to retrieve the data from the table records-- 
SELECT other.Kontakt_id, 
	   rec.klient_id,
	   other.Total,
	   rec.pracownik_id,	      
	   rec.kontakt_ts,
	   rec.status
FROM [TASKDWH].[dbo].[records] rec
RIGHT JOIN
--check the count of all items
--and take the max of the kontakt id as the last recent
(SELECT COUNT(*) AS Total, 
		klient_id, 
		MAX(kontakt_id) Kontakt_id
FROM [TASKDWH].[dbo].[records]
--group the results by klient-id
GROUP BY klient_id
--taking just klient with count more than 2 (at least 3)
HAVING COUNT(*) > 2) other 
ON other.Kontakt_id = rec.kontakt_id

--------------------------
----------TASK 3----------
--------------------------

--Create the DimTime in the schema inside our database
USE TASKDWH
GO
SELECT rec.kontakt_id AS [KONTAKT_ID], DAY(rec.kontakt_ts) AS [DAY],
DATENAME(weekday, rec.kontakt_ts) AS [DayOfWeek], MONTH(rec.kontakt_ts)AS [Month],
DATENAME(month, rec.kontakt_ts) AS [MonthName], DATENAME(QUARTER, rec.kontakt_ts) AS [Quarter], 
YEAR(rec.kontakt_ts) AS [Year]
INTO [TASK].[DIMTime]
FROM [dbo].[records] rec

USE TASKDWH
GO
ALTER TABLE [TASK].[DIMTime] ADD PRIMARY KEY(KONTAKT_ID)


-- Create the fact table
USE TASKDWH
GO
CREATE TABLE [TASK].FactTask (
date DATETIME NOT NULL DEFAULT (GETDATE()),
sukcesy int,
utraty int, 
do_ponowienia int
)
-- After creating the fact table, I just insert set all values to 0 for columns except the date one


--Inserting the value in the fact table
--First I declare a cursor--
DECLARE cursor_status CURSOR
--Create the query to have the tabe, where I will retrieve the data to insert in the fact
--The data are coming from [TASKDWH].[dbo].[records]
-- columns kontakt_id, klient_id, status
FOR SELECT
		  other.Kontakt_id, 
	      rec.klient_id,  
		  rec.status
FROM [TASKDWH].[dbo].[records] rec
RIGHT JOIN
(SELECT COUNT(*) AS Total, 
		klient_id, 
		MAX(kontakt_id) Kontakt_id
FROM [TASKDWH].[dbo].[records]

GROUP BY klient_id) other 
ON other.Kontakt_id = rec.kontakt_id;
--End of the query

OPEN cursor_status;

FETCH NEXT FROM cursor_status INTO
--create variable variables to store the values from the precedent query => coming from the cursor
@kontakt_id, 
@klient_id,
@status;
--while this condition is true (when fetch is a success)
WHILE @@FETCH_STATUS = 0

BEGIN 
	UPDATE
	--update our fact table
		[TASKDWH].[TASK].[FactTask]
		--set the values of the column from fact table
		--update the value everytime when the match occurs (increase value by 1)
			SET 
				sukcesy = CASE WHEN @status = 'zainteresowany' THEN sukcesy + 1 ELSE sukcesy END,
				utraty = CASE WHEN @status = 'niezainterosowany' THEN utraty + 1 ELSE sukcesy END,
				do_ponowienia = CASE WHEN @status = 'poczta_glosowa' or @status='nie_ma_w_domu' THEN do_ponowienia + 1 ELSE do_ponowienia END
	--proceed with the next item in the cursor
	FETCH NEXT FROM cursor_status INTO
		@kontakt_id, 
		@klient_id,
		@status;
END;
--close the cursor
CLOSE cursor_status;
--deallocate
DEALLOCATE cursor_status;