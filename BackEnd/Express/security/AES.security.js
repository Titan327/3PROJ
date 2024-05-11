const crypto = require('crypto');

function crypt(key,string){
    // creation de iv (comme le salt)
    const iv = crypto.randomBytes(16);
    // creation du chiffreur (cipher)
    const cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key,'hex'), iv);
    // chiffrage
    let encrypted = cipher.update(string);
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    return iv.toString('hex') + ':' + encrypted.toString('hex');
}


function decrypt(key,string){

    const crypt = string.split(':');
    // recup iv
    const iv = Buffer.from(crypt.shift(), 'hex');
    const encryptedText = Buffer.from(crypt.join(':'), 'hex');
    // decrypt
    const decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(key,'hex'), iv);
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    return decrypted.toString("utf-8");

}

const genKey = async (req, res) => {
    const aesKey = crypto.randomBytes(32);

    console.log('Clé AES générée:', aesKey.toString('hex'));

    res.status(200).send("ok")
}



module.exports = {
    crypt,
    decrypt,
    genKey
}