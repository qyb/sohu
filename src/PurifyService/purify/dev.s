let SessionLoad = 1
if &cp | set nocp | endif
let s:cpo_save=&cpo
set cpo&vim
inoremap <silent> <S-Tab> =BackwardsSnippet()
inoremap <C-CR> A;
inoremap <S-Space> la
inoremap <S-CR> =SkipPair()
imap <F7> :e!
imap <F6> :s=^\(//\)*==g:noh
imap <F5> :s=^\(//\)*=//=g:noh
snoremap <silent> 	 i<Right>=TriggerSnippet()
snoremap  b<BS>
nmap   /=expand("<cword>")
snoremap % b<BS>%
snoremap ' b<BS>'
map <silent> ,mm :ShowMarksPlaceMark
map <silent> ,ma :ShowMarksClearAll
map <silent> ,mh :ShowMarksClearMark
map <silent> ,mo :ShowMarksOn
map <silent> ,mt :ShowMarksToggle
map ,mbt <Plug>TMiniBufExplorer
map ,mbu <Plug>UMiniBufExplorer
map ,mbc <Plug>CMiniBufExplorer
map ,mbe <Plug>MiniBufExplorer
nmap ,ihn :IHN
nmap ,is :IHS:A
nmap ,ih :IHS
nnoremap ,call :CCallHierarchy
nmap ,ct :call QFixToggle(1)
nmap ,co :QFix
noremap <silent> ,a9 :b19
noremap <silent> ,a8 :b18
noremap <silent> ,a7 :b17
noremap <silent> ,a6 :b16
noremap <silent> ,a5 :b15
noremap <silent> ,a4 :b14
noremap <silent> ,a3 :b13
noremap <silent> ,a2 :b12
noremap <silent> ,a1 :b11
noremap <silent> ,a0 :b10
noremap <silent> ,9 :b9
noremap <silent> ,8 :b8
noremap <silent> ,7 :b7
noremap <silent> ,6 :b6
noremap <silent> ,5 :b5
noremap <silent> ,4 :b4
noremap <silent> ,3 :b3
noremap <silent> ,2 :b2
noremap <silent> ,1 :b1
nmap <silent> ,wq :wqa! 
nmap <silent> ,q :qa! 
nmap <silent> ,w :wa! 
map ,cp :cp
map ,cn :cn
map ,cl :cl
map ,bc :close
map ,bd :bdelete
map ,vs :vs
map ,l l 
map ,h h
map ,k k
map ,j j
map <silent> A :bp
map <silent> S :bn
snoremap U b<BS>U
snoremap \ b<BS>\
snoremap ^ b<BS>^
snoremap ` b<BS>`
nmap gx <Plug>NetrwBrowseX
nmap wm :WMToggle
snoremap <Left> bi
snoremap <Right> a
snoremap <BS> b<BS>
snoremap <silent> <S-Tab> i<Right>=BackwardsSnippet()
nnoremap <silent> <Plug>NetrwBrowseX :call netrw#NetrwBrowseX(expand("<cWORD>"),0)
map <silent> <PageDown> :bn
map <silent> <PageUp> :bp
nmap <silent> <Down> -:let t:flwwinlayout = winrestcmd()
nmap <silent> <Up> +:let t:flwwinlayout = winrestcmd()
nmap <silent> <Right> >:let t:flwwinlayout = winrestcmd()
nmap <silent> <Left> <:let t:flwwinlayout = winrestcmd()
nmap <Nul>i :cs find i ^=expand("<cfile>")$
nmap <Nul>d :cs find d  =expand("<cword>")
nmap <Nul>f :cs find f  =expand("<cfile>")
nmap <Nul>e :cs find e  =expand("<cword>")
nmap <Nul>t :cs find t  =expand("<cword>")
nmap <Nul>c :cs find c  =expand("<cword>")
nmap <Nul>g :cs find g  =expand("<cword>")
nmap <Nul>s :cs find s  =expand("<cword>")
nnoremap <silent> <F12> :Rgrep
nnoremap <silent> <F11> :Bgrep
nmap <F9> :run macros/gdb_mappings.vim
nmap <F8> :mksession! dev.s
nmap <F7> :e!
vmap <F7> :e!
nmap <F6> :s=^\(//\)*==g:noh
vmap <F6> :s=^\(//\)*==g:noh
nmap <F5> :s=^\(//\)*=//=g:noh
vmap <F5> :s=^\(//\)*=//=g:noh
map <F4> :!
map <F3> :make
map <F2> :!ctags --exclude=.svn -R --c++-kinds=+p --fields=+iaS --extra=+q --language-force=C++
inoremap <silent> 	 =TriggerSnippet()
inoremap <NL> 
inoremap <silent> 	 =ShowAvailableSnips()
inoremap ( ()i
inoremap ) =ClosePair(')')
imap ,ihn :IHN
imap ,is :IHS:A
imap ,ih :IHS
inoremap ;; A;
inoremap [ []i
inoremap ] =ClosePair(']')
inoremap { =ClsoeBrace()
inoremap } =ClosePair('}')
let &cpo=s:cpo_save
unlet s:cpo_save
set autoindent
set autoread
set background=dark
set backspace=2
set browsedir=buffer
set cindent
set clipboard=autoselect,exclude:cons\\|linux,unnamed
set cmdheight=2
set completeopt=menuone,menu,longest
set confirm
set cscopeprg=/usr/bin/cscope
set cscopetag
set cscopetagorder=1
set cscopeverbose
set encoding=cp936
set noequalalways
set errorfile=/tmp/vIG3Fak/1
set expandtab
set fileencodings=utf-8
set fileformats=unix,dos,mac
set fillchars=vert:\ ,stl:\ ,stlnc:\ 
set formatoptions=tcrqnmM
set guifont=courier\ 10
set hidden
set history=1000
set incsearch
set iskeyword=@,48-57,_,192-255,$,%,#,-
set laststatus=2
set lazyredraw
set linespace=1
set makeef=error.err
set path=.,/usr/include/*,,
set report=0
set ruler
set rulerformat=%20(%2*%<%f%=%m%r%3l%c%p%%%)
set scrolloff=10
set selection=exclusive
set selectmode=mouse,key
set shiftwidth=4
set shortmess=atI
set showcmd
set showmatch
set smartindent
set smarttab
set softtabstop=4
set statusline=%2*%-3.3n%0*\ %f\ %h%1*%m%r%w%0*[%{strlen(&ft)?&ft:'none'},%{&encoding},%{&fileformat}]%=%2*0x%-8B\ %-14.(%l,%c%V%)\ %<%P
set tabstop=4
set tags=./tags,~/.vim/cpptags,tags,~/vim/share/vim/vim73/doc/tags,~/.vim/doc/tags
set viminfo='100,<50,s10,h,!
set whichwrap=b,s,<,>
set wildignore=*.pyc
set wildmenu
let s:so_save = &so | let s:siso_save = &siso | set so=0 siso=0
let v:this_session=expand("<sfile>:p")
silent only
cd ~/work/purify
if expand('%') == '' && !&modified && line('$') <= 1 && getline(1) == ''
  let s:wipebuf = bufnr('%')
endif
set shortmess=aoO
badd +78 ./protocols.py
badd +40 session.py
badd +2 testbed.py
badd +59 chain.py
badd +32 interfaces.py
badd +28 logger.conf
badd +23 rulers.py
badd +17 crawler.py
badd +8 logger.py
badd +27 store.py
args ./protocols.py
edit rulers.py
set splitbelow splitright
wincmd _ | wincmd |
split
1wincmd k
wincmd w
set nosplitbelow
set nosplitright
wincmd t
set winheight=1 winwidth=1
exe '1resize ' . ((&lines * 1 + 20) / 40)
exe '2resize ' . ((&lines * 35 + 20) / 40)
argglobal
enew
file -MiniBufExplorer-
let s:cpo_save=&cpo
set cpo&vim
nnoremap <buffer> 	 :call search('\[[0-9]*:[^\]]*\]'):<BS>
nnoremap <buffer> h :call search('\[[0-9]*:[^\]]*\]','b'):<BS>
nnoremap <buffer> j gj
nnoremap <buffer> k gk
nnoremap <buffer> l :call search('\[[0-9]*:[^\]]*\]'):<BS>
nnoremap <buffer> p :wincmd p:<BS>
nnoremap <buffer> <S-Tab> :call search('\[[0-9]*:[^\]]*\]','b'):<BS>
nnoremap <buffer> <Up> gk
nnoremap <buffer> <Down> gj
let &cpo=s:cpo_save
unlet s:cpo_save
setlocal keymap=
setlocal noarabic
setlocal autoindent
setlocal balloonexpr=
setlocal nobinary
setlocal bufhidden=delete
setlocal nobuflisted
setlocal buftype=nofile
setlocal cindent
setlocal cinkeys=0{,0},0),:,0#,!^F,o,O,e
setlocal cinoptions=
setlocal cinwords=if,else,while,do,for,switch
set colorcolumn=80
setlocal colorcolumn=80
setlocal comments=s1:/*,mb:*,ex:*/,://,b:#,:%,:XCOMM,n:>,fb:-
setlocal commentstring=/*%s*/
setlocal complete=.,w,b,u,t,i
setlocal concealcursor=
set conceallevel=2
setlocal conceallevel=2
setlocal completefunc=
setlocal nocopyindent
setlocal cryptmethod=
setlocal nocursorbind
setlocal nocursorcolumn
set cursorline
setlocal cursorline
setlocal define=
setlocal dictionary=
setlocal nodiff
setlocal equalprg=
setlocal errorformat=
setlocal expandtab
if &filetype != ''
setlocal filetype=
endif
setlocal foldcolumn=0
setlocal foldenable
setlocal foldexpr=0
setlocal foldignore=#
setlocal foldlevel=0
setlocal foldmarker={{{,}}}
setlocal foldmethod=manual
setlocal foldminlines=1
setlocal foldnestmax=20
setlocal foldtext=foldtext()
setlocal formatexpr=
setlocal formatoptions=tcrqnmM
setlocal formatlistpat=^\\s*\\d\\+[\\]:.)}\\t\ ]\\s*
setlocal grepprg=
setlocal iminsert=2
setlocal imsearch=2
setlocal include=
setlocal includeexpr=
setlocal indentexpr=
setlocal indentkeys=0{,0},:,0#,!^F,o,O,e
setlocal noinfercase
setlocal iskeyword=@,48-57,_,192-255,$,%,#,-
setlocal keywordprg=
setlocal nolinebreak
setlocal nolisp
setlocal nolist
setlocal makeprg=
setlocal matchpairs=(:),{:},[:]
setlocal modeline
setlocal nomodifiable
setlocal nrformats=octal,hex
set number
setlocal nonumber
setlocal numberwidth=4
setlocal omnifunc=
setlocal path=
setlocal nopreserveindent
setlocal nopreviewwindow
setlocal quoteescape=\\
setlocal noreadonly
setlocal norelativenumber
setlocal norightleft
setlocal rightleftcmd=search
setlocal noscrollbind
setlocal shiftwidth=4
setlocal noshortname
setlocal smartindent
setlocal softtabstop=4
setlocal nospell
setlocal spellcapcheck=
setlocal spellfile=
setlocal spelllang=en
setlocal statusline=
setlocal suffixesadd=
setlocal noswapfile
setlocal synmaxcol=3000
if &syntax != ''
setlocal syntax=
endif
setlocal tabstop=4
setlocal tags=
setlocal textwidth=0
setlocal thesaurus=
setlocal noundofile
setlocal nowinfixheight
setlocal nowinfixwidth
set nowrap
setlocal wrap
setlocal wrapmargin=0
wincmd w
argglobal
setlocal keymap=
setlocal noarabic
setlocal autoindent
setlocal balloonexpr=
setlocal nobinary
setlocal bufhidden=
setlocal buflisted
setlocal buftype=
setlocal cindent
setlocal cinkeys=0{,0},0),:,!^F,o,O,e
setlocal cinoptions=
setlocal cinwords=if,else,while,do,for,switch
set colorcolumn=80
setlocal colorcolumn=80
setlocal comments=s1:/*,mb:*,ex:*/,://,b:#,:XCOMM,n:>,fb:-
setlocal commentstring=#%s
setlocal complete=.,w,b,u,t,i
setlocal concealcursor=
set conceallevel=2
setlocal conceallevel=2
setlocal completefunc=
setlocal nocopyindent
setlocal cryptmethod=
setlocal nocursorbind
setlocal nocursorcolumn
set cursorline
setlocal cursorline
setlocal define=
setlocal dictionary=
setlocal nodiff
setlocal equalprg=
setlocal errorformat=
setlocal expandtab
if &filetype != 'python'
setlocal filetype=python
endif
setlocal foldcolumn=0
setlocal foldenable
setlocal foldexpr=0
setlocal foldignore=#
setlocal foldlevel=0
setlocal foldmarker={{{,}}}
setlocal foldmethod=manual
setlocal foldminlines=1
setlocal foldnestmax=20
setlocal foldtext=foldtext()
setlocal formatexpr=
setlocal formatoptions=tcrqnmM
setlocal formatlistpat=^\\s*\\d\\+[\\]:.)}\\t\ ]\\s*
setlocal grepprg=
setlocal iminsert=2
setlocal imsearch=2
setlocal include=s*\\(from\\|import\\)
setlocal includeexpr=substitute(v:fname,'\\.','/','g')
setlocal indentexpr=
setlocal indentkeys=0{,0},:,!^F,o,O,e
setlocal noinfercase
setlocal iskeyword=@,48-57,_,192-255,$,%,#,-
setlocal keywordprg=
setlocal nolinebreak
setlocal nolisp
setlocal nolist
setlocal makeprg=
setlocal matchpairs=(:),{:},[:]
setlocal modeline
setlocal modifiable
setlocal nrformats=octal,hex
set number
setlocal number
setlocal numberwidth=4
setlocal omnifunc=pythoncomplete#Complete
setlocal path=
setlocal nopreserveindent
setlocal nopreviewwindow
setlocal quoteescape=\\
setlocal noreadonly
setlocal norelativenumber
setlocal norightleft
setlocal rightleftcmd=search
setlocal noscrollbind
setlocal shiftwidth=4
setlocal noshortname
setlocal smartindent
setlocal softtabstop=4
setlocal nospell
setlocal spellcapcheck=
setlocal spellfile=
setlocal spelllang=en
setlocal statusline=
setlocal suffixesadd=.py
setlocal swapfile
setlocal synmaxcol=3000
if &syntax != 'python'
setlocal syntax=python
endif
setlocal tabstop=4
setlocal tags=
setlocal textwidth=0
setlocal thesaurus=
setlocal noundofile
setlocal winfixheight
setlocal nowinfixwidth
set nowrap
setlocal nowrap
setlocal wrapmargin=0
silent! normal! zE
let s:l = 29 - ((22 * winheight(0) + 17) / 35)
if s:l < 1 | let s:l = 1 | endif
exe s:l
normal! zt
29
normal! 022l
wincmd w
2wincmd w
exe '1resize ' . ((&lines * 1 + 20) / 40)
exe '2resize ' . ((&lines * 35 + 20) / 40)
tabnext 1
if exists('s:wipebuf')
  silent exe 'bwipe ' . s:wipebuf
endif
unlet! s:wipebuf
set winheight=1 winwidth=20 shortmess=atI
let s:sx = expand("<sfile>:p:r")."x.vim"
if file_readable(s:sx)
  exe "source " . fnameescape(s:sx)
endif
let &so = s:so_save | let &siso = s:siso_save
doautoall SessionLoadPost
unlet SessionLoad
" vim: set ft=vim :
