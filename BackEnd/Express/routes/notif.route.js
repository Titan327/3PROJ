const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security');
const notif = require('../controllers/notif.controller');

router.get("/", securityMiddleware.verifyIsAuth, notif.GetAllNotifByUser);
router.delete("/", securityMiddleware.verifyIsAuth, notif.DeleteNotifById);
router.get("/count", securityMiddleware.verifyIsAuth, notif.GetNumNotifByUser);

module.exports = router;
