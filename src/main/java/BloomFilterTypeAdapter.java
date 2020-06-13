import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;

public class BloomFilterTypeAdapter extends TypeAdapter<BloomFilter> {
    @Override
    public void write(JsonWriter out, BloomFilter bloomFilter) throws IOException {
        out.beginObject();
        out.name("num_hash_functions").value(bloomFilter.getNumberOfHashFunctions());
        out.name("bits").value(Base64.toBase64String(bloomFilter.getBackingBytes()));
        out.endObject();
    }

    @Override
    public BloomFilter read(JsonReader in) throws IOException {
        in.beginObject();

        byte[] bits = null;
        int num_hash_functions = 0;

        String fieldname;

        while (in.hasNext()) {
            JsonToken token = in.peek();

            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldname = in.nextName();
            } else {
                // should never happen
                throw new JsonParseException("No field name?!");
            }


            if ("num_hash_functions".equals(fieldname) && num_hash_functions == 0) {
                num_hash_functions = in.nextInt();
                if (num_hash_functions < 1)
                    throw new JsonParseException("Invalid number of hash functions! Expected a value > 1, actual value is " + num_hash_functions);
            } else if ("bits".equals(fieldname) && bits == null) {
                bits = Base64.decode(in.nextString());
                if (bits.length < 1)
                    throw new JsonParseException("bits has invalid length " + bits.length);
            } else {
                throw new JsonParseException("Unexpected field : " + fieldname);
            }
        }

        if (bits == null) {
            throw new JsonParseException("Missing bits field!");
        }
        if (num_hash_functions < 1) {
            throw new JsonParseException("Missing num_hash_functions field!");
        }

        in.endObject();


        return new BloomFilter(num_hash_functions, bits);
    }
}
