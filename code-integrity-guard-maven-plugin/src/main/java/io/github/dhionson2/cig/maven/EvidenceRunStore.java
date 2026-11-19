package io.github.dhionson2.cig.maven;

import java.io.*;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/** Disk protocol shared across Maven goals and forked test JVMs. */
final class EvidenceRunStore {
    static final String FORMAT="2";
    private EvidenceRunStore() {}
    static Properties read(Path manifest) throws IOException { Properties p=new Properties(); if(!Files.isRegularFile(manifest))throw new IOException("Evidence run manifest is missing"); try(InputStream in=Files.newInputStream(manifest)){p.load(in);} if(!FORMAT.equals(p.getProperty("format")))throw new IOException("Unsupported evidence manifest format"); return p; }
    static void write(Path manifest,Properties p) throws IOException { Path parent=manifest.toAbsolutePath().normalize().getParent();Files.createDirectories(parent);Path temp=Files.createTempFile(parent,"cig-run-",".tmp");try(OutputStream out=Files.newOutputStream(temp)){p.store(out,"Code Integrity Guard evidence run");}try{Files.move(temp,manifest,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(temp,manifest,StandardCopyOption.REPLACE_EXISTING);} }
    static String sha256(Path path) throws IOException { try { MessageDigest d=MessageDigest.getInstance("SHA-256");if(Files.exists(path))try(InputStream in=Files.newInputStream(path)){byte[] b=new byte[8192];for(int n;(n=in.read(b))>=0;)d.update(b,0,n);}return HexFormat.of().formatHex(d.digest()); }catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);} }
    static String required(Properties p,String key) throws IOException {String v=p.getProperty(key);if(v==null||v.isBlank())throw new IOException("Missing evidence manifest field: "+key);return v;}
    static void lock(Path path, IOAction action) throws IOException {Files.createDirectories(path.toAbsolutePath().normalize().getParent());try(FileChannel c=FileChannel.open(path,StandardOpenOption.CREATE,StandardOpenOption.READ,StandardOpenOption.WRITE);FileLock ignored=c.lock()){action.run(c);}}
    static String sha256(FileChannel channel)throws IOException{try{MessageDigest d=MessageDigest.getInstance("SHA-256");channel.force(false);channel.position(0);ByteBuffer b=ByteBuffer.allocate(8192);while(channel.read(b)>=0){b.flip();d.update(b);b.clear();}return HexFormat.of().formatHex(d.digest());}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    @FunctionalInterface interface IOAction {void run(FileChannel channel) throws IOException;}
}
