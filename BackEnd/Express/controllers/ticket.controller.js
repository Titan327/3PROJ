

const postTicket = async (req, res) => {

    console.log("ca arrive ici");
    return res.status(200).send(req.file.buffer);

}

module.exports={
    postTicket
}