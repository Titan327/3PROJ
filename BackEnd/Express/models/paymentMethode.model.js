const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const payementMethodeSchema = new mongoose.Schema({
    type: {
        type: String,
        require: true
    },
    userId: {
        type: Number,
        require: true
    },
    value: Schema.Types.Mixed
}, { versionKey: false });


const payementMethode = mongoose.model('payementMethode', payementMethodeSchema);

module.exports = payementMethode;