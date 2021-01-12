/*
    * Reference code
    # <include name="java/external/msgpack-core-0.8.20.jar"/>
*/

public static boolean saveToBinary(List<float[]> embeddings, String destination) {
    try (OutputStream out = new FileOutputStream(destination)) {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        for (float[] obj : embeddings) {
            packer.packBinaryHeader(obj.length * 4);
            packer.writePayload(floatArray2ByteArray(obj));
        }
        packer.close();
    } catch (IOException e) {
        LOG.error("Could not save embeddings file", e);
        return false;
    } catch (NullPointerException e){
        LOG.error("Some embedding is null.", e);
        return false;
    }
    return true;
}

public static List<float[]> loadFromBinary(String source) {
    List<float[]> embeddings = new ArrayList<>();
    try (InputStream in = new FileInputStream(source)) {

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(in);
        while (unpacker.hasNext()) {
            byte[] bytes = new byte[unpacker.unpackBinaryHeader()];
            unpacker.readPayload(bytes);
            float[] floats = byteArrayToFloatArray(bytes);
            embeddings.add(floats);
        }

        unpacker.close();
        return embeddings;

    } catch (IOException e) {
        LOG.error("Could not read file at " + source, e);
        return null;
    }
}

private static byte[] floatArray2ByteArray(float[] values){
        byte[] balue = new byte[values.length * 4];

        for (int i =0; i < values.length; i++)
            ByteBuffer.wrap(balue, i*4, 4).putFloat(values[i]);

        return balue;
    }


private static float[] byteArrayToFloatArray(byte[] bytes) {
    float[] floats = new float[bytes.length/4];

    for (int i=0; i < bytes.length; i+=4)
        floats[i/4] = ByteBuffer.wrap(bytes, i, 4).getFloat();

    return floats;
}


private static List<Integer> readLabels(String inputFile){
    List<Integer> labels = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
        String line = br.readLine();
        while (line != null) {
            int clusterLabel = Integer.parseInt(line.trim());
            labels.add(clusterLabel);
            line = br.readLine();
        }
    } catch (IOException e) {
        LOG.error(e);
        return null;
    }

    LOG.info("Found " + labels.size() + " labels from clusterInput.");
    return labels;
}