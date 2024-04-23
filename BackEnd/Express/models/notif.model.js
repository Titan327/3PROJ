const mongoose = require('mongoose');

const notifSchema = new mongoose.Schema({
    user_id: Number,
    message: String,
    link: String,
    date: { type: Date, default: Date.now },
    seen: Boolean
}, { versionKey: false });


const NotifSchema = mongoose.model('Notif', notifSchema);


module.exports = NotifSchema;