const BankInfo = require("../models/bankInfo.model")

const Get = async (req,res) => {

    try {
        const bankInfo = await BankInfo.find();
        return res.status(200).send(bankInfo);
    } catch (e) {
        console.error(e);
        return e;
    }

}

module.exports={
    Get
}