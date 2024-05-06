const Notif = require('../models/notif.model');
const UserGroup = require("../models/userGroup.model");
const Message = require("../models/message.model");

async function CreateNotif (user_id,message,link) {
    await Notif.create(
        {
            user_id: user_id,
            message: message,
            link: link,
            seen: false
        }
    )
}

const GetAllNotifByUser = async (req,res) => {
    try {
        const userId = req.authorization.userId;

        const allNotif = await Notif.find({user_id: userId});

        await Notif.updateMany({user_id: userId,seen: 0},{seen: true});

        return res.status(200).json({allNotif});
    }catch (e){
        return res.status(500).json({e});
    }
}



const GetNumNotifByUser = async (req,res) => {
    try {
        const userId = req.authorization.userId;

        const count = await Notif.countDocuments({user_id: userId,seen: 0});

        return res.status(200).json({count});
    }catch (e){
        return res.status(500).json({ message: 'Internal server error' });
    }
}

const DeleteNotifById = async (req,res) => {
    try{
        const {id_notif} = req.body;
        const notifExist =  await Notif.findOneAndDelete({_id: id_notif,user_id: req.authorization.userId });
        if (notifExist){
            return res.status(200).json({ message: 'Notification deleted' });
        }else {
            return res.status(449).json({ error: "This notification is not linked to your account or has already been deleted." });
        }

    }catch (e){
        console.log(e);
        return res.status(500).json({ message: 'Internal error server' });
    }

}

module.exports={
    CreateNotif,
    GetAllNotifByUser,
    GetNumNotifByUser,
    DeleteNotifById
}