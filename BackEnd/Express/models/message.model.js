const mongoose = require('mongoose');


const messageSchema = new mongoose.Schema({
    text: String,
    userId: Number,
    groupId: String,
    timestamp: { type: Date, default: Date.now }
}, { versionKey: false });


const Message = mongoose.model('Message', messageSchema);

module.exports = Message;