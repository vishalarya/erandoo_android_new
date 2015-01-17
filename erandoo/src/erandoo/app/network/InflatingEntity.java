package erandoo.app.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class InflatingEntity implements HttpEntity {

	HttpEntity wrappedEntity;
	
    public InflatingEntity(HttpEntity wrapped) {
    	this.wrappedEntity = wrapped;
    } 

    @Override 
    public InputStream getContent() throws IOException {
        return new GZIPInputStream(wrappedEntity.getContent());
    } 

    @Override 
    public long getContentLength() { 
        return wrappedEntity.getContentLength(); 
    } 
	@Override
	public void consumeContent() throws IOException {

	}

	@Override
	public Header getContentEncoding() {
		// TODO Auto-generated method stub
		return wrappedEntity.getContentEncoding();
	}

	@Override
	public Header getContentType() {
		return wrappedEntity.getContentType();	
	}

	@Override
	public boolean isChunked() {
		// TODO Auto-generated method stub
		return wrappedEntity.isChunked();
	}

	@Override
	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return wrappedEntity.isRepeatable();
	}

	@Override
	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return wrappedEntity.isStreaming();
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		// TODO Auto-generated method stub

	}

}
