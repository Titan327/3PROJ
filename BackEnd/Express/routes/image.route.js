const router = require('express').Router();
const multer = require('multer');
const pp = require('../controllers/profilePicture.controller');
const resize = require('../middlewares/resize.middleware');
const securityMiddleware = require("../security/middleware.security");
const ticket = require("../controllers/ticket.controller");
const upload = multer();

//POST http://localhost:9002/api/img/test
router.post("/upload/profile-picture", securityMiddleware.verifyIsAuth, upload.single('image'),resize,pp.uploadUserPic);
router.get("/profile-picture/:userId/:size",pp.GetUserPic);

router.post("/upload/group-picture/:groupId", securityMiddleware.verifyIsAuth, upload.single('image'),resize,pp.uploadGroupPic);
router.get("/group-picture/:groupId/:size",pp.GetGroupPic);

router.get("/group-picture/:groupId/:size",pp.GetGroupPic);

router.post("/ticket/:groupId/:transactionId",securityMiddleware.verifyIsAuth,upload.single('file'), ticket.postTicket );
router.get("/ticket/:groupId/:transactionId",securityMiddleware.verifyIsAuth, ticket.getTicketByName );

router.post("/rib/:userId",securityMiddleware.verifyIsAuth,upload.single('file'), ticket.postTicket );
router.get("/rib/:userId",securityMiddleware.verifyIsAuth, ticket.getTicketByName );

module.exports = router;