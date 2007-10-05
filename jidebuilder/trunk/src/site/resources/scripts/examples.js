var stateMap = {}
function showOrHideCode( key ){
   if( typeof stateMap[key] == "undefined" ){
      stateMap[key] = false
   }

   if( !stateMap[key] ){
      Effect.BlindDown( key )
   }else{
      Effect.BlindUp( key )
   }
   stateMap[key] = !stateMap[key] 
}
