const sharp = require("sharp");
module.exports = async (req,res,next) => {

    if (!req.file) {
        return res.status(400).json({ error: 'Aucun fichier trouvé' });
    }

    const sizes = [100, 200, 500];
    const imagePromises = sizes.map(size => {
        return sharp(req.file.buffer)
            .resize(size, size)
            .toBuffer()
            .then(buffer => ({ name: size, buffer: buffer }));
    });

    Promise.all(imagePromises)
        .then(images => {
            req.resizedImages = images;
            next();
        })
        .catch(err => {
            res.status(500).json({ error: 'Erreur lors du redimensionnement des images' });
        });
}