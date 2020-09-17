//package com.capston.recipe.for_dev
//
//lateinit var auth :FirebaseAuth
//lateinit var authListener : FirebaseAuth.AuthStateListener
//lateinit var googleSigneInClient : GoogleSignInClient //구글 로그인을 관리하는 클래스
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//
//    // Configure Google Sign In
//    //GoogleSignInOptions 옵션을 관리해주는 클래스로 API 키값과 요청할 값이 저장되어 있다.
//    var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(getString(R.string.default_web_client_id))
//        .requestEmail()
//        .build();
//
//    googleSigneInClient=GoogleSignIn.getClient(this,gso)
//}
//private fun signIn(){
//    val signInIntent =googleSigneInClient.signInIntent
//    startActivityForResult(signInIntent,100)
//}
