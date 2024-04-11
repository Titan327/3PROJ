const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security');
const MessageController = require('../controllers/message.controller');

router.post("/:groupId", securityMiddleware.verifyIsAuth, MessageController.Post);
router.get("/:groupId", securityMiddleware.verifyIsAuth, MessageController.Get);
router.delete("/", securityMiddleware.verifyIsAuth, MessageController.Delete);

module.exports = router;
