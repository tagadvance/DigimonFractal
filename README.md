# DigimonFractal

```
100 /* func sample. coast creation */
110 float s
120 while s<1 or s>=2
130     input "ratio 1 to 2";s
140 endwhile
150 s = (s-1)/10+1
160 screen 1,2,1,1
170 s=sqr(s*s-1)
180 float x0=100, x1=412, y0=0, y1=0
190 fractal(x0,x1,y0,y1,1)
200 line(100, 50, 412, 50, 255, 65535)
210 end
220 func fractal(x0:float,x1:float,y0:float,y1:float,sp:int)
230     float l, r, x2, y2
240     l=sqr((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0))
250     if l<2 or sp>=9 then {
260         line(x0,y0/3+50,x1,y1/3+50,255,65535) : return()
270     }
280     r=rnd()+rnd()+rnd()-2
290     x2=(x0+x1)/2+s*(y1-y0)*r
300     y2=(y0+y1)/2+s*(x0-x1)*r
310     sp = sp + 1
320     fractal(x0,x2,y0,y2,sp)
330     fractal(x2,x1,y2,y1,sp)
340 endfunc
```
### Sources
* [Digimon](http://fingswotidun.com/code/index.php/Digimon) uses the [Creative Commons Attribution-ShareAlike 3.0 License](http://creativecommons.org/licenses/by-sa/3.0/) 
* [Running Izzy's "Digivolving" Code](http://digitalworldproblems.tumblr.com/post/76036641581/while-im-looking-at-that-moviecode-post-he)