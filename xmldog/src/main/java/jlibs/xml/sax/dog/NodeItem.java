/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.xml.sax.dog;

import jlibs.core.io.IOUtil;
import jlibs.core.lang.ImpossibleException;
import jlibs.xml.Namespaces;
import jlibs.xml.dom.DOMNavigator;
import jlibs.xml.sax.dog.sniff.Event;
import jlibs.xml.xsl.TransformerUtil;
import org.jaxen.dom.NamespaceNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Santhosh Kumar T
 */
public class NodeItem implements NodeType{
    public static final NodeItem NODEITEM_DOCUMENT = new NodeItem();

    public final int type;
    public final String location; // unique xpath
    public final String value;
    public final String localName;
    public final String namespaceURI;
    public final String qualifiedName;

    public long order;
    public int refCount;
    public Object xml;
    public boolean xmlBuilt;

    public NodeItem(){
        order = 0;
        type = DOCUMENT;
        location = "/";
        value = null;
        localName = null;
        namespaceURI = null;
        qualifiedName = null;
    }

    public NodeItem(Event event){
        order = event.order();
        type = event.type();
        location = event.location();
        value = event.value();
        localName = event.localName();
        namespaceURI = event.namespaceURI();
        qualifiedName = event.qualifiedName();
    }

    // used only for testing purposes
    public NodeItem(Node node, NamespaceContext nsContext){
        if(node instanceof Attr && Namespaces.URI_XMLNS.equals(node.getNamespaceURI()))
            type = NAMESPACE;
        else if(node.getNodeType()==NamespaceNode.NAMESPACE_NODE)
            type = NAMESPACE;
        else
            type = node.getNodeType();
        location = new DOMNavigator().getXPath(node, nsContext);
        value = node.getNodeValue();

        localName = node.getLocalName();
        namespaceURI = node.getNamespaceURI();
        qualifiedName = node.getNodeName();
        xml = node;
    }

    // used only for testing purposes
    public NodeItem(int type, String location, String value, String localName, String namespaceURI, String qualifiedName){
        this.type = type;
        this.location = location;
        this.value = value;
        this.localName = localName;
        this.namespaceURI = namespaceURI;
        this.qualifiedName = qualifiedName;
    }

    // used only for testing purposes
    public NodeItem(Node node, String prefix, String uri, NamespaceContext nsContext){
        type = NAMESPACE;

        location = new DOMNavigator().getXPath(node, nsContext)+"/namespace::"+prefix;
        value = uri;

        localName = prefix;
        namespaceURI = Namespaces.URI_XMLNS;
        qualifiedName = "xmlns:"+prefix;
        xml = node;
    }

    public void printTo(PrintStream out){
        if(xml instanceof Node){
            out.println(location);
            try{
                Transformer transformer = TransformerUtil.newTransformer(null, true, 0, null);
                transformer.transform(new DOMSource((Node)xml), new StreamResult(out));
            }catch(TransformerException ex){
                throw new RuntimeException(ex);
            }
        }else
            out.print(location);
    }

    @Override
    public String toString(){
        if(xml instanceof Node){
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            printTo(new PrintStream(bout, true));
            try{
                return bout.toString(IOUtil.UTF_8.name());
            }catch(UnsupportedEncodingException ex){
                throw new ImpossibleException(ex);
            }
        }else
            return location;
    }
}
