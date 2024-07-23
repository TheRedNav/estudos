#!/usr/bin/env bash
NOTE='> :exclamation: Dê um feedback para esse documento no rodapé.[^1]\n'
URL='[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/:tag:.md&internalidade=:tag:)'

for f in $(find . -name '*.md'); 
do
	tag=`echo $f | sed -e "s/\.\/\(.*\)\.md/\1/g"`
	echo "file: [" $f "] tag: [" $tag "]"
	line=`echo $NOTE | sed "s|:tag:|$tag|g"`
	echo $line
	sed -i "1 i $line" $f
	final=`echo $URL | sed "s|:tag:|$tag|g "`
	blank="---"
	echo $blank ", file: [" $f "]"
	echo "$blank" >> $f
	echo $final ", file: [" $f "]"
	echo $final >> $f
done

