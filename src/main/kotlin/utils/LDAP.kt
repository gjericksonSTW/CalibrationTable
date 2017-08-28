package LDAP

import java.util.*
import javax.naming.*
import javax.naming.directory.*
import javax.naming.ldap.InitialLdapContext
import javax.naming.ldap.LdapContext
import kotlin.collections.ArrayList

class LDAP {

    companion object {

        private var directory : ArrayList<String> = ArrayList()
        private var staticDir: ArrayList<String> = ArrayList()
        private lateinit var ctx : DirContext

        fun initialize () {
            try {
                var env: Hashtable<Any, Any> = Hashtable()
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
                env.put(Context.PROVIDER_URL, "ldap://winservices1.shelltech.works:389/dc=shelltech,dc=works")
                env.put(Context.REFERRAL, "throw")
                env.put(Context.SECURITY_AUTHENTICATION, "simple")
                env.put(Context.SECURITY_PRINCIPAL, "SHELLTECH\\bind")
                env.put(Context.SECURITY_CREDENTIALS, "B1nd\$TW!")
                try {
                    ctx = InitialDirContext(env)
                    searchAttr(env, staticDir)
                }catch (e : AuthenticationNotSupportedException){
                    println(e)
                }catch( e : AuthenticationException) {
                    println(e)
                }
            }catch (e : NamingException){
                println(e)
            }
            disconnect()
        }

        private fun searchAttr(env : Hashtable<Any, Any>, dir: ArrayList<String>){
            var ctx : LdapContext = InitialLdapContext(env, null)
            var namingEnum : NamingEnumeration<SearchResult> = ctx.search("ou=Employees,ou=STW", "(objectCategory=Person)", getSimpleSearchControls())
            while(namingEnum.hasMore()){
                var result : SearchResult = namingEnum.next()
                var attrs : Attributes = result.attributes
                var name : String = attrs.get("sAMAccountName").toString()
                name = name.substring(16, name.length)
                dir.add(name)
            }
        }

        private fun getSimpleSearchControls () : SearchControls {
            var searchControls : SearchControls = SearchControls()
            searchControls.searchScope = SearchControls.SUBTREE_SCOPE
            searchControls.timeLimit = 30000
            return searchControls
        }
        private fun disconnect(){
            try{
                if(ctx != null)
                    ctx.close()
            }catch(e : NamingException){
                println(e)
            }
        }

        private fun connect( username: String,
                        password: String ) :Boolean {
            try {
                var env: Hashtable<Any, Any> = Hashtable()
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
                env.put(Context.PROVIDER_URL, "ldap://winservices1.shelltech.works:389/dc=shelltech,dc=works")
                env.put(Context.REFERRAL, "throw")
                env.put(Context.SECURITY_AUTHENTICATION, "simple")
                env.put(Context.SECURITY_PRINCIPAL, username)
                env.put(Context.SECURITY_CREDENTIALS, password)
                try {
                    ctx = InitialDirContext(env)
                    println("Connected\r\n")
                    searchAttr(env, directory)
                    return true
                }catch (e : AuthenticationNotSupportedException){
                    println(e)
                }catch( e : AuthenticationException) {
                    println(e)
                }
            }catch (e : NamingException){
                println(e)
            }
            disconnect()
            return false
        }

        fun clearDir(){
            directory.clear()
        }

        fun checkUsername(username: String) : Boolean{
            return staticDir.contains(username)
        }

        fun checkPassword(password: String, username: String):Boolean {
            return connect("SHELLTECH\\$username", password)
        }
    }
}