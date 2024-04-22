const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security');
const InvitationController = require('../controllers/invitation.controller');

//PUBLIC
//POST http://localhost:9002/{token}
router.post("/:token", securityMiddleware.verifyIsAuth, InvitationController.joinGroup);
//POST http://localhost:9002/api/groups/{id}/create
router.post("/api/groups/:groupId/createInvitation", securityMiddleware.verifyIsAuth, InvitationController.createInvitation);

module.exports = router;
