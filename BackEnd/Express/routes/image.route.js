const router = require('express').Router();
const multer = require('multer');
const pp = require('../controllers/pp.controller');
const resize = require('../middlewares/resize.middleware');
const securityMiddleware = require("../security/middleware.security");
const upload = multer();

//POST http://localhost:9002/api/img/test
router.post("/upload/profile-picture", securityMiddleware.verifyIsAuth, upload.single('image'),resize,pp.uploadPic);
router.get("/profile-picture/:userId/:size",pp.GetPic);

module.exports = router;