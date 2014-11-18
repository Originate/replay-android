package io.replay.framework;

import android.test.AndroidTestCase;

/**
 * Created by parthpadgaonkar on 11/11/14.
 */
public class ConfigTest extends AndroidTestCase {

    private Config baseConfig;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        baseConfig = new Config(true, true, "testKey", 5000, 100, 1000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConfigAPIKeyConstructor() throws Exception {
        Config c = new Config("key");
        assertNotNull(c);

        try{
            new Config("");
            fail("Config should not accept a blank API key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(null);
            fail("Config should not accept a null API key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    public void testConfigConstructor() throws Exception {
        try{
            new Config(true, true, null, 5000, 100, 1000);
            fail("Config should not accept a null API key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "", 5000, 100, 1000);
            fail("Config should not accept an empty api key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 100, 100, 1000);
            fail("Config should not accept a small dispatch interval value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 9000000, 100, 1000);
            fail("Config should not accept an excessive dispatch interval value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 5000, 5, 1000);
            fail("Config should not accept a small flush value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 5000, 100, 1000);
            fail("Config should not accept an outlandish value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 5000, 100, 1000);
            fail("Config should not accept an outlandish value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            new Config(true, true, "testKey", 5000, 100, 1000);
            fail("Config should not accept an outlandish value");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }



    }

    public void testSetAPIKey() throws Exception {
        try{
            final String key = "key";
            baseConfig.setApiKey(key);
            assertEquals(key, baseConfig.getApiKey());
        }catch (IllegalArgumentException e){
            fail("Config should accept a (semi valid) API key");
        }

        try{
            baseConfig.setApiKey(null);
            fail("Config should not accept a null API key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            baseConfig.setApiKey("");
            fail("Config should not accept a null API key");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    public void testSetDispatchInterval() throws Exception {
        try{
            baseConfig.setDispatchInterval(5000);
            assertEquals(5000, baseConfig.getDispatchInterval());
        }catch (IllegalArgumentException e){
            fail("Config should accept a valid dispatch interval");
        }

        try{
            baseConfig.setDispatchInterval(100);
            fail("Config should reject a dispatch interval that is too small");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            baseConfig.setDispatchInterval(30*60*1000+5);
            fail("Config should reject a dispatch interval that is too large");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    public void testSetFlush() throws Exception {
        try{
            baseConfig.setFlushAt(105);
            assertEquals(5000, baseConfig.getDispatchInterval());
        }catch (IllegalArgumentException e){
            fail("Config should accept a valid flush interval");
        }

        try{
            baseConfig.setFlushAt(10);
            fail("Config should reject a flush interval that is too small");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            baseConfig.setFlushAt(90000);
            fail("Config should reject a flush interval that is too large");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    public void testSetMaxQueue() throws Exception {
        try{
            baseConfig.setMaxQueue(5000);
            assertEquals(5000, baseConfig.getDispatchInterval());
        }catch (IllegalArgumentException e){
            fail("Config should accept a valid maxqueue size");
        }

        try{
            baseConfig.setMaxQueue(10);
            fail("Config should reject a maxqueue size that is too small");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }

        try{
            baseConfig.setMaxQueue(90000);
            fail("Config should reject a maxqueue size that is too large");
        }catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }
}
