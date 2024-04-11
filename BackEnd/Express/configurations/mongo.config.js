const mongoose = require("mongoose");

mongoose.connect("mongodb://admin:admin@API-MONGO:27017/app?authSource=admin");

const db = mongoose.connection;

db.on("error", console.error.bind(console, "MongoDB connection error:"));

db.once("open", function () {
    console.log("Connected to MongoDB database");
});
