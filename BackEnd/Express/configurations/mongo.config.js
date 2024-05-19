const mongoose = require("mongoose");

mongoose.connect("mongodb://"+process.env.MONGO_USER+":"+process.env.MONGO_PASSWORD+"@"+process.env.MONGO_NAME+":"+process.env.MONGO_PORT+"/"+process.env.MONGO_DB_NAME+"?authSource=admin");



const db = mongoose.connection;

db.on("error", console.error.bind(console, "MongoDB connection error:"));

db.once("open", function () {
    console.log("Connected to MongoDB database");
});
