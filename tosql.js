var mysql=require('mysql');

var con = mysql.createConnection({
	host:"localhost",
	user:"root",
	password:"root"
});


//Connecting to the database
con.connect(function(err){
	if(err) throw err;

	//Creating main database
	console.log("Connected to database");
	con.query("CREATE DATABASE IF NOT EXISTS elemental", function(err,result){
		if(err) console.log("error:"+err);
		else console.log("Database created");
	});


});