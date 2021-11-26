import java.security.Permission

/**
 * [NoExitSecurityManager] is a modified [SecurityManager],
 * which disallows exiting at any moment.
 *
 * It is used to test cases, where the program has to call exitProcess()
 */
class NoExitSecurityManager : SecurityManager() {
    override fun checkPermission(perm: Permission?) {
        // allow anything
    }
    override fun checkPermission(perm: Permission?, context: Any?) {
        // allow anything
    }
    override fun checkExit(status: Int) {
        super.checkExit(status)
        throw Exception("Exit block")
    }
}
