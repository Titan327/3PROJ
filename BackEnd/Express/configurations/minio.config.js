const Minio = require('minio');

const minioClient = new Minio.Client({
    endPoint: process.env.MINIO_ENDPOINT,
    port: parseInt(process.env.MINIO_PORT),
    useSSL: false,
    accessKey: process.env.MINIO_ACCESS_KEY,
    secretKey: process.env.MINIO_SECRET_KEY,
});

async function initializeBucket(BucketName) {
    try {
        const bucketExists = await minioClient.bucketExists(BucketName);
        if (!bucketExists) {
            await minioClient.makeBucket(BucketName);
            console.log(`Bucket "${BucketName}" créé avec succès.`);
        } else {
            console.log(`Le bucket "${BucketName}" existe déjà.`);
        }
    } catch (error) {
        console.error('Erreur lors de l\'initialisation du bucket:', error);

    }
}


module.exports = {
    minioClient,
    initializeBucket
};