package jlibs.core.graph;

/**
 * @author Santhosh Kumar T
 */
public final class Path{
    private Path parentPath;
    private Object elem;
    private int index;

    public Path(Object elem){
        if(elem==null)
            throw new IllegalArgumentException("element in path must be non null");
        this.elem = elem;
        this.index = 0;
    }

    private Path(Path parentPath, Object elem, int index){
        this.parentPath = parentPath;
        this.elem = elem;
        this.index = index;
    }

    public Path append(Object elem){
        return append(elem, -1);
    }

    public Path append(Object elem, int index){
        if(elem==null)
            throw new IllegalArgumentException("element in path must be non null");
        return new Path(this, elem, index);
    }

    public Path getParentPath(){
        return parentPath;
    }

    public Object getElement(){
        return elem;
    }

    public int getIndex(){
        return index;
    }

    public Object getElement(int i){
        if(i<0)
            throw new IndexOutOfBoundsException("negative index: "+i);
        int len = getLength();
        if(i>=len)
            throw new IndexOutOfBoundsException(String.format("index %d is out of range", i));
        len--;
        Path path = this;
        while(len!=i)
            path = path.parentPath;
        return path.elem;
    }

    public int getLength(){
        int len = 0;
        for(Path path=this; path!=null; path=path.parentPath)
            len++;
        return len;
    }

    public Object[] toArray(){
        Object array[] = new Object[getLength()];
        Path path = this;
        for(int i=getLength(); i>0; i--){
            array[--i] = path.elem;
            path = path.parentPath;
        }
        return array;
    }

    public String toString(){
        StringBuilder buff = new StringBuilder();
        for(Path path=this; path!=null; path=path.parentPath){
            if(buff.length()>0)
                buff.insert(0, ", ");
            buff.insert(0, path.elem.toString());
        }
        return buff.toString();
    }
}