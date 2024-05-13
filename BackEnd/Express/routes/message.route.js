const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security');
const MessageController = require('../controllers/message.controller');

router.post("/:groupId", securityMiddleware.verifyIsAuth, MessageController.Post);
router.get("/:groupId", securityMiddleware.verifyIsAuth, MessageController.Get);
router.delete("/", securityMiddleware.verifyIsAuth, MessageController.Delete);

router.post("/private/:groupId/:user1/:user2", securityMiddleware.verifyIsAuth, MessageController.PostUserToUser);
router.get("/private/:groupId/:user1/:user2", securityMiddleware.verifyIsAuth, MessageController.GetUserToUser);
router.delete("/private/:groupId/:user1/:user2", securityMiddleware.verifyIsAuth, MessageController.DeleteUserToUser);

module.exports = router;
