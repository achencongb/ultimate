int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
goto loc_2;
loc_2:
 if (nondet_bool()) {
  goto loc_0;
 }
 goto end;
loc_0:
 if (nondet_bool()) {
  goto loc_1;
 }
 goto end;
loc_1:
end:
;
}
