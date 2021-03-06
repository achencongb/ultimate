type ref;
type realVar;
type classConst;
// type Field x;
// var $HeapVar : <x>[ref, Field x]x;

const unique $null : ref ;
const unique $intArrNull : [int]int ;
const unique $realArrNull : [int]realVar ;
const unique $refArrNull : [int]ref ;

const unique $arrSizeIdx : int;
var $intArrSize : [int]int;
var $realArrSize : [realVar]int;
var $refArrSize : [ref]int;

var $stringSize : [ref]int;

//built-in axioms 
axiom ($arrSizeIdx == -1);

//note: new version doesn't put helpers in the perlude anymore//Prelude finished 



var int$GrowTreeR.Random$index0 : int;
var java.lang.String$lp$$rp$$GrowTreeR.Random$args257 : [int]ref;
var GrowTreeR.TreeList$GrowTreeR.TreeList$next256 : Field ref;
var GrowTreeR.Tree$GrowTreeR.TreeList$value255 : Field ref;
var GrowTreeR.TreeList$GrowTreeR.Tree$children254 : Field ref;


// procedure is generated by joogie.
function {:inline true} $neref(x : ref, y : ref) returns (__ret : int) {
if (x != y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $realarrtoref($param00 : [int]realVar) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $modreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $leref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $modint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $gtref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 2
// <GrowTreeR.GrowTreeR: void <init>()>
procedure void$GrowTreeR.GrowTreeR$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r01 : ref;
Block16:
	r01 := __this;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 3
	 call void$java.lang.Object$$la$init$ra$$28((r01));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqrealarray($param00 : [int]realVar, $param11 : [int]realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addint(x : int, y : int) returns (__ret : int) {
(x + y)
}


// procedure is generated by joogie.
function {:inline true} $subref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $inttoreal($param00 : int) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrint($param00 : int, $param11 : int) returns (__ret : int);



	 //  @line: 4
// <GrowTreeR.GrowTreeR: void main(java.lang.String[])>
procedure void$GrowTreeR.GrowTreeR$main$2229($param_0 : [int]ref)
  modifies java.lang.String$lp$$rp$$GrowTreeR.Random$args257, $stringSize;
 {
var r02 : [int]ref;
var $i03 : int;
var r15 : ref;
var $r26 : ref;
Block17:
	r02 := $param_0;
	 //  @line: 5
	java.lang.String$lp$$rp$$GrowTreeR.Random$args257 := r02;
	 //  @line: 7
	 call $i03 := int$GrowTreeR.Random$random$2238();
	 //  @line: 7
	 call r15 := GrowTreeR.Tree$GrowTreeR.Tree$createTree$2235(($i03));
	 assert ($neref((r15), ($null))==1);
	 //  @line: 8
	$r26 := $HeapVar[r15, GrowTreeR.TreeList$GrowTreeR.Tree$children254];
	 //  @line: 8
	 call void$GrowTreeR.GrowTreeR$growList$2231(($r26));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $negReal($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrint($param00 : int, $param11 : int) returns (__ret : int);



	 //  @line: 21
// <GrowTreeR.GrowTreeR: void growList(GrowTreeR.TreeList)>
procedure void$GrowTreeR.GrowTreeR$growList$2231($param_0 : ref) {
var $r113 : ref;
var $r214 : ref;
var r012 : ref;
Block31:
	r012 := $param_0;
	 goto Block32;
	 //  @line: 22
Block32:
	 goto Block35, Block33;
	 //  @line: 22
Block35:
	 //  @line: 22
	 assume ($negInt(($neref((r012), ($null))))==1);
	 return;
	 //  @line: 22
Block33:
	 assume ($neref((r012), ($null))==1);
	 goto Block34;
	 //  @line: 25
Block34:
	 assert ($neref((r012), ($null))==1);
	 //  @line: 25
	$r113 := $HeapVar[r012, GrowTreeR.Tree$GrowTreeR.TreeList$value255];
	 goto Block36;
	 //  @line: 25
Block36:
	 //  @line: 25
	 call void$GrowTreeR.GrowTreeR$growTree$2230(($r113));
	 assert ($neref((r012), ($null))==1);
	 //  @line: 26
	$r214 := $HeapVar[r012, GrowTreeR.TreeList$GrowTreeR.TreeList$next256];
	 //  @line: 26
	 call void$GrowTreeR.GrowTreeR$growList$2231(($r214));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $refarrtoref($param00 : [int]ref) returns (__ret : ref);



	 //  @line: 18
// <GrowTreeR.Tree: GrowTreeR.Tree createTree(int)>
procedure GrowTreeR.Tree$GrowTreeR.Tree$createTree$2235($param_0 : int) returns (__ret : ref) {
var i026 : int;
var i230 : int;
var i432 : int;
var i331 : int;
var $r123 : ref;
var r024 : ref;
var $r229 : ref;
var $i128 : int;
Block42:
	i230 := $param_0;
	 goto Block43;
	 //  @line: 19
Block43:
	 goto Block44, Block46;
	 //  @line: 19
Block44:
	 assume ($gtint((i230), (0))==1);
	 goto Block45;
	 //  @line: 19
Block46:
	 //  @line: 19
	 assume ($negInt(($gtint((i230), (0))))==1);
	 //  @line: 20
	__ret := $null;
	 return;
	 //  @line: 22
Block45:
	 //  @line: 22
	i331 := $subint((i230), (1));
	 goto Block47;
	 //  @line: 24
Block47:
	 //  @line: 24
	$r123 := $newvariable((48));
	 assume ($neref(($newvariable((48))), ($null))==1);
	 assert ($neref(($r123), ($null))==1);
	 //  @line: 24
	 call void$GrowTreeR.Tree$$la$init$ra$$2232(($r123));
	 //  @line: 24
	r024 := $r123;
	 //  @line: 25
	 call i026 := int$GrowTreeR.Random$random$2238();
	 //  @line: 27
	i432 := 0;
	 goto Block49;
	 //  @line: 27
Block49:
	 goto Block50, Block52;
	 //  @line: 27
Block50:
	 assume ($geint((i432), (i026))==1);
	 goto Block51;
	 //  @line: 27
Block52:
	 //  @line: 27
	 assume ($negInt(($geint((i432), (i026))))==1);
	 //  @line: 28
	$i128 := $subint((i331), (1));
	 //  @line: 28
	 call $r229 := GrowTreeR.Tree$GrowTreeR.Tree$createTree$2235(($i128));
	 assert ($neref((r024), ($null))==1);
	 //  @line: 28
	 call void$GrowTreeR.Tree$addChild$2233((r024), ($r229));
	 //  @line: 27
	i432 := $addint((i432), (1));
	 goto Block49;
	 //  @line: 31
Block51:
	 //  @line: 31
	__ret := r024;
	 return;
}


	 //  @line: 9
// <GrowTreeR.Tree: void addChild(GrowTreeR.Tree)>
procedure void$GrowTreeR.Tree$addChild$2233(__this : ref, $param_0 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r118 : ref;
var $r319 : ref;
var $r217 : ref;
var r016 : ref;
Block38:
	r016 := __this;
	r118 := $param_0;
	 //  @line: 10
	$r217 := $newvariable((39));
	 assume ($neref(($newvariable((39))), ($null))==1);
	 assert ($neref((r016), ($null))==1);
	 //  @line: 10
	$r319 := $HeapVar[r016, GrowTreeR.TreeList$GrowTreeR.Tree$children254];
	 assert ($neref(($r217), ($null))==1);
	 //  @line: 10
	 call void$GrowTreeR.TreeList$$la$init$ra$$2236(($r217), (r118), ($r319));
	 assert ($neref((r016), ($null))==1);
	 //  @line: 10
	$HeapVar[r016, GrowTreeR.TreeList$GrowTreeR.Tree$children254] := $r217;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $divref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $mulref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $neint(x : int, y : int) returns (__ret : int) {
if (x != y) then 1 else 0
}


	 //  @line: 6
// <GrowTreeR.Random: int random()>
procedure int$GrowTreeR.Random$random$2238() returns (__ret : int)
  modifies int$GrowTreeR.Random$index0, $stringSize;
 {
var $i241 : int;
var $i037 : int;
var $r138 : [int]ref;
var r039 : ref;
var $i140 : int;
var $i342 : int;
	 //  @line: 7
Block55:
	 //  @line: 7
	$r138 := java.lang.String$lp$$rp$$GrowTreeR.Random$args257;
	 //  @line: 7
	$i037 := int$GrowTreeR.Random$index0;
	 assert ($geint(($i037), (0))==1);
	 assert ($ltint(($i037), ($refArrSize[$r138[$arrSizeIdx]]))==1);
	 //  @line: 7
	r039 := $r138[$i037];
	 //  @line: 8
	$i140 := int$GrowTreeR.Random$index0;
	 //  @line: 8
	$i241 := $addint(($i140), (1));
	 //  @line: 8
	int$GrowTreeR.Random$index0 := $i241;
	$i342 := $stringSize[r039];
	 //  @line: 9
	__ret := $i342;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $ltreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftorefarr($param00 : ref) returns (__ret : [int]ref);



// procedure is generated by joogie.
function {:inline true} $gtint(x : int, y : int) returns (__ret : int) {
if (x > y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $reftoint($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addref($param00 : ref, $param11 : ref) returns (__ret : ref);



	 //  @line: 13
// <GrowTreeR.Tree: GrowTreeR.Tree createNode()>
procedure GrowTreeR.Tree$GrowTreeR.Tree$createNode$2234() returns (__ret : ref) {
var $r120 : ref;
var r021 : ref;
	 //  @line: 14
Block40:
	 //  @line: 14
	$r120 := $newvariable((41));
	 assume ($neref(($newvariable((41))), ($null))==1);
	 assert ($neref(($r120), ($null))==1);
	 //  @line: 14
	 call void$GrowTreeR.Tree$$la$init$ra$$2232(($r120));
	 //  @line: 14
	r021 := $r120;
	 //  @line: 15
	__ret := r021;
	 return;
}


// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



// procedure is generated by joogie.
function {:inline true} $xorreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// <java.lang.String: int length()>
procedure int$java.lang.String$length$59(__this : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $cmpreal(x : realVar, y : realVar) returns (__ret : int) {
if ($ltreal((x), (y)) == 1) then 1 else if ($eqreal((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $addreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $gtreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqreal(x : realVar, y : realVar) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ltint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $newvariable($param00 : int) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $divint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geint(x : int, y : int) returns (__ret : int) {
if (x >= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $mulint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $leint(x : int, y : int) returns (__ret : int) {
if (x <= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $shlref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqrefarray($param00 : [int]ref, $param11 : [int]ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftointarr($param00 : ref) returns (__ret : [int]int);



// procedure is generated by joogie.
function {:inline true} $ltref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $mulreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $divreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



	 //  @line: 11
// <GrowTreeR.GrowTreeR: void growTree(GrowTreeR.Tree)>
procedure void$GrowTreeR.GrowTreeR$growTree$2230($param_0 : ref)
  modifies $HeapVar;
 {
var $r411 : ref;
var $r29 : ref;
var $r18 : ref;
var $r310 : ref;
var r07 : ref;
Block18:
	r07 := $param_0;
	 goto Block19;
	 //  @line: 12
Block19:
	 goto Block22, Block20;
	 //  @line: 12
Block22:
	 //  @line: 12
	 assume ($negInt(($neref((r07), ($null))))==1);
	 return;
	 //  @line: 12
Block20:
	 assume ($neref((r07), ($null))==1);
	 goto Block21;
	 //  @line: 14
Block21:
	 assert ($neref((r07), ($null))==1);
	 //  @line: 14
	$r18 := $HeapVar[r07, GrowTreeR.TreeList$GrowTreeR.Tree$children254];
	 goto Block23;
	 //  @line: 14
Block23:
	 goto Block24, Block26;
	 //  @line: 14
Block24:
	 assume ($neref(($r18), ($null))==1);
	 goto Block25;
	 //  @line: 14
Block26:
	 //  @line: 14
	 assume ($negInt(($neref(($r18), ($null))))==1);
	 //  @line: 15
	$r310 := $newvariable((27));
	 assume ($neref(($newvariable((27))), ($null))==1);
	 //  @line: 15
	$r411 := $newvariable((28));
	 assume ($neref(($newvariable((28))), ($null))==1);
	 assert ($neref(($r411), ($null))==1);
	 //  @line: 15
	 call void$GrowTreeR.Tree$$la$init$ra$$2232(($r411));
	 assert ($neref(($r310), ($null))==1);
	 //  @line: 15
	 call void$GrowTreeR.TreeList$$la$init$ra$$2236(($r310), ($r411), ($null));
	 assert ($neref((r07), ($null))==1);
	 //  @line: 15
	$HeapVar[r07, GrowTreeR.TreeList$GrowTreeR.Tree$children254] := $r310;
	 goto Block29;
	 //  @line: 17
Block25:
	 assert ($neref((r07), ($null))==1);
	 //  @line: 17
	$r29 := $HeapVar[r07, GrowTreeR.TreeList$GrowTreeR.Tree$children254];
	 goto Block30;
	 //  @line: 19
Block29:
	 return;
	 //  @line: 17
Block30:
	 //  @line: 17
	 call void$GrowTreeR.GrowTreeR$growList$2231(($r29));
	 goto Block29;
}


// procedure is generated by joogie.
function {:inline true} $orint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftorealarr($param00 : ref) returns (__ret : [int]realVar);



// procedure is generated by joogie.
function {:inline true} $cmpref(x : ref, y : ref) returns (__ret : int) {
if ($ltref((x), (y)) == 1) then 1 else if ($eqref((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $realtoint($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $orreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqint(x : int, y : int) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ushrref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $eqintarray($param00 : [int]int, $param11 : [int]int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negRef($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $lereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $nereal(x : realVar, y : realVar) returns (__ret : int) {
if (x != y) then 1 else 0
}


	 //  @line: 3
// <GrowTreeR.Random: void <clinit>()>
procedure void$GrowTreeR.Random$$la$clinit$ra$$2239()
  modifies int$GrowTreeR.Random$index0;
 {
	 //  @line: 4
Block56:
	 //  @line: 4
	int$GrowTreeR.Random$index0 := 0;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $instanceof($param00 : ref, $param11 : classConst) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $orref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $intarrtoref($param00 : [int]int) returns (__ret : ref);



	 //  @line: 1
// <GrowTreeR.Random: void <init>()>
procedure void$GrowTreeR.Random$$la$init$ra$$2237(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r036 : ref;
Block54:
	r036 := __this;
	 assert ($neref((r036), ($null))==1);
	 //  @line: 2
	 call void$java.lang.Object$$la$init$ra$$28((r036));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $subreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shlreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negInt(x : int) returns (__ret : int) {
if (x == 0) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $gereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 5
// <GrowTreeR.TreeList: void <init>(GrowTreeR.Tree,GrowTreeR.TreeList)>
procedure void$GrowTreeR.TreeList$$la$init$ra$$2236(__this : ref, $param_0 : ref, $param_1 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r033 : ref;
var r235 : ref;
var r134 : ref;
Block53:
	r033 := __this;
	r134 := $param_0;
	r235 := $param_1;
	 assert ($neref((r033), ($null))==1);
	 //  @line: 6
	 call void$java.lang.Object$$la$init$ra$$28((r033));
	 assert ($neref((r033), ($null))==1);
	 //  @line: 7
	$HeapVar[r033, GrowTreeR.Tree$GrowTreeR.TreeList$value255] := r134;
	 assert ($neref((r033), ($null))==1);
	 //  @line: 8
	$HeapVar[r033, GrowTreeR.TreeList$GrowTreeR.TreeList$next256] := r235;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqref(x : ref, y : ref) returns (__ret : int) {
if (x == y) then 1 else 0
}


	 //  @line: 4
// <GrowTreeR.Tree: void <init>()>
procedure void$GrowTreeR.Tree$$la$init$ra$$2232(__this : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r015 : ref;
Block37:
	r015 := __this;
	 assert ($neref((r015), ($null))==1);
	 //  @line: 5
	 call void$java.lang.Object$$la$init$ra$$28((r015));
	 assert ($neref((r015), ($null))==1);
	 //  @line: 6
	$HeapVar[r015, GrowTreeR.TreeList$GrowTreeR.Tree$children254] := $null;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $cmpint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else if (x == y) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $andint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shlint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $subint(x : int, y : int) returns (__ret : int) {
(x - y)
}


