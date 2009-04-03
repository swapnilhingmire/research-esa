package edu.uka.aifb.api.document;

public interface IMetaDataDocument extends IDocument {

	public Object getMetaData( String key );

	public void setMetaData( String key, Object value );

}
