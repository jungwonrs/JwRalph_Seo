
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.*;

public class temp {

    public static void main (String[] args){
    String temp = "txPool&&1&&[{\"index\":\"1_0.15\",\"data\":\"{\\\"msg\\\":\\\"1430351332\\\",\\\"time\\\":\\\"1568963131\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101008a5b32b4e066eb050ea66c6ea70a6cb120c649ddde4c9f4149f28215b07cd46995a7e7a2849e14e784ee298df0ab55af5be80d54e8688389a5f601ed40b329b1302dd7b8743b1711a4010f2c22aaaccb3eb96836b8cc97ffa5ee33df6f7a860bcfced4e8815fd2c712cc0463b0414089282fbc5e1ef0d1f26aaac0762515b89b08666900a84079acd04e56574ba43a598dec8bf11024b252e8b6a9030bd08d2d8ef808a5be640802ae476ec6ac33fc20236e5e2e930f21d834918d7347364cce969534646d06a376b6941209257c7b13349a12ddd178ce31137cac5f913e4d32d76708cf1dec1ab9661ed53ce97e95898d6cab9b30b63a0bc817594505ff17a50203010001\\\",\\\"sig\\\":\\\"NQ4vkvSlETDBaMC5OOxiqTJUtndEe1J2N4RYPFOa6RyGaRZtdjg77/v1jefBjsKhPPfkWaP8WQj6hYYGyQqBolx7F0DvU4vCm+puFAEH8nuhH79eIxPFhLPZwH0Oq410hrEvNf98hBMG5uLxUMz1GYRua6I4YkWUxyKnxBFU/TDSkB9CVHxK2CQL+yBWCUSdN1F5P7wDf7zi2lA4uirtGvkL1pQ4JTSfWa/8iCULX4OMkgpF64OzfmtfpgtyogDoTw2JqUMf2mWs7qYelZ1kf7qjtgBz0t/dVJPD2Em9zOb0251GJvIn6JA0g9+fmwQH+PcUv0TCqqmRZZHn7o33ow\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.16\",\"data\":\"{\\\"msg\\\":\\\"491405816\\\",\\\"time\\\":\\\"1568963132\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100adf21b151a346dd059b831ced8466a9fa0c9015e14966e0266ee1ec8a4abbaf17a95d84818bef2d1023070d218955a6b742ee8844a1c68885181c280d9716949ec6d3d2691eaef03c56ca6730f6a1b3bb962d8a7af8cebd92cfdb827acf38ab8c5d71bcededfb0ed53259a1ed72debf3610a1f1c9442cfcf27b5b9a033d65f318ac22c8fa27f348308580fea2127f44667fd94fa655613ef17d90ac2b0d2bca1e25d7f398ea1e89f3718938d53608ca8cf8fc57bc274cacd583a031c35e790146f98f86f97ad0b60c30ef4fe3f51cb87c7ab6fe3105a56a213d4b83f38a4b0779d7b6b34495369123b3b753f99e5e72959e1260ca59b10706fedf66684039f810203010001\\\",\\\"sig\\\":\\\"jmX73KE6e4NHSWdPU2m+ay3+i+tRgD1tEk1XpyWwMmttwLPqg4VVEjzaBMIg45KodCAIQ5LOBtTA9qdNMG/OWwt2BvMrIkvJXLXy0kUb3b0sryru+FVbHvya+rx11YOSVsipOOkvuMpG9sl62A4fyLKgY7yA9hpj/WPXuBSl3Bfx+d+cFFHWZlYUzaDqFS/Q3OxMksoObhZ11YwDtXVDA0xeL3J+AgHcN3myAHpjYQftQko6qxPLPo4atXyAIivdiCmNWCzC+X1zmvitEvO9F+nTcMc4jRifTKbBAzK1bFK5u9gCExRHTGNK2ACwF1yYPDnQU5LwrwcEr7icybMnJg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.17\",\"data\":\"{\\\"msg\\\":\\\"1516180528\\\",\\\"time\\\":\\\"1568963133\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100c560c99453bb00cb2699544ac51a4ccbfc2c07241f7dbea5c579a4114a1994ab70326cd2ea1bf143f3792c78bcfa5f46a5f8fef51f2a5526472dec65161604cc4c78569ed8f328d5affe956f16bb43923e419551e2bd1ffaf8f49594e45f93ab51935b38e728efe4e1398422a74f78f1afbfe92814fd8a4f64331b114b647e0fed47a5e50ea7dc5a1e67802873c1fd34c27e6662d926e74031d2c9b0fa593454acd9e4b6b3061f7f30cea845c78800a94dedbf673e94a0ffa74da8c770f8d7be198f6ac1638aeaa70528c067168c9903dd14631eccfba995ceccc0fc6910811476cec0319ebbb6c967e0f7a6d835755490739ced4b5e256e87b4e7cc76fa7e770203010001\\\",\\\"sig\\\":\\\"RXX2XsGIe9nR0cpU+lXXx2wjQ0/mMTHoIdgDD2+WdFh8qqL3iLu8na74bKi7NbSdrg8M3KlDUBL86GrZ6gysZyMAyn4/oVSl7S9kiAUBdbA8uiTRFHPnZbpiPMiAaR4dV43VwKp/s+bjZ6JqjAa0adWokw0wv5g7b30zw+Xe+SywWoQdg3eQrR989V60P9Djf3/Vfpaw36RblY5tN7Q7uepmxH3kr8Nr7uGP8XXHPYdC/C3XxntLSmi2uf68Y/Zv/ZPO7DhoSev5HJSdQoWWEi/o2/gbOifVXYd1oNwwmxrRkJcl5Dho9zDt/U5IIHu/ctA/Aw+2B5d+wqralBwXMw\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.18000000000000002\",\"data\":\"{\\\"msg\\\":\\\"-1455747013\\\",\\\"time\\\":\\\"1568963134\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100c467d85d9e641ee570a7c9d6c426cd9eeee4e753086e8309e8579869816269f747573d9a539fb49d248d883c84e1c72269cc831ecf684f3ee34dfa4c5472b42fae0fa76e1f0b22ff5b6a5f5695811600bb069d566759e52e049ff0fffd1fa94db509514833a4c94d226625f6b2e2d086b8237f6872345cbc6c30bd2550ffc071a0acd2efed3c94fe549a151e2f76270c7dfbbaa3a78eb53cd58ac5c4a33120ccad1472f414184503a4aebf2f14938018cf569881bf8949ddd83632262f5db4b20f14c7416b06c5075bf8d185cad1c73c2f4414d081680541bf812abde388e8d185fca7c0a221642df34e718ba7e2c01634893b30db02db9e1888bf1cd25bc63f0203010001\\\",\\\"sig\\\":\\\"Xmoaw6ELYN4QYzRbnVx+B9em/P2MmIVjuXANE22XsqIbHX5v9CtXLnC9ctnqnuTQl+PaMPQ79RSLl2RMdC+OD/TqjJs/SYipo2V2jgVq2S+Cy5OYJdobcvu/yIkscuT0CKBc6L0ZTNsyC+lvpVDLwjSBAg4lRog8HErdIkxvToqqbyVbCvPdDggT2wRtKe0WE6eNUFQI95RrzBVaWVYXYgkL0DGOUm6mVkpovhMKpSeWNnB6dknhpe6OdVE+yRE4x5SE1Bt3XhBT/TqzdCY1T2kCjhHh+/n5meLD5h/koGJD6aqD+Fyk3Dakqgv3JghjGYlrL0+uxUOnqycHDfkCOA\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.19000000000000003\",\"data\":\"{\\\"msg\\\":\\\"-1038346812\\\",\\\"time\\\":\\\"1568963135\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100b682d6a6fc8e38bcd80294b8a40689c4b01638b086fddfa3ff250784aae36433c018c7b9660be6a63c80f975db23406e421c8f09db3cda54c6cd818aad481f33428e993a92a65806a735c02b788bac08777fa2e57b16776462f659895bed3211945783bba72efc00fa818721ef9397aca0085c0d530d643312ef9be0b22f9443e3b2d27fd0add5a6a9fc4d00cfa3edb73e7b8fa4a27ced356d14223bf0ddf0d0eb16815e25350a76e73f1fb159534d53073014037de2aaea5119b86676717215f17c1e3d5e18028e77dab818282aec690f97db524df35053aa5daa4f712ecd2867e536cd32b133185dbc3308944572120bd23af30821f5467c36d7f49805c4b30203010001\\\",\\\"sig\\\":\\\"FJhCNoGvoNnBqPi6HZ36UqocuSy5nkW6VM2S9KDmyFo504CwRBNdLSpMLaSMj3jleS+9GEqLF9O8aW8YjxirZaNLz8IJTn76BKGC/oev/OrBVIE+2FoBbIGc4fnFgezlq4TbysEdW+vZ/+6SaLkkBr5gHKmXdGjHXo5+0t7NgU4FPpNEA47o5r1CjxI76Wc04H7uYbTAnPx8bCbK8DzZ7jZwVpEluRwykW9THsfrfW6NpY2MmQZCSXminv4TeMWMfTHmrk+66/0xQXPtZXi4qmsbmY1ZlzFEFJN0WsY+Z3JCXzA4iJpGQENw62RdWhAR0pTkHAHIt9EeKD8QvYTVyg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.20000000000000004\",\"data\":\"{\\\"msg\\\":\\\"1533248531\\\",\\\"time\\\":\\\"1568963136\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100946355613640c70344bde78f9446319f6d081cb5eee13b697b3705a887c98a6a29d0c57096ab880fe9946a1c60fc4ba5933b1441dded4fb626f6d7971eaf62891ddee208ff4376f2cf63494e916eacf3973420c508ba2736f58042d7057935f856fccc1c87aee0baa136a7c5e11e2daa31225929116690649713b17b71fe77a30216f32b6db1e826d6d4f059821fa961517a98e5b550a410972f284571917c9edd2e06d315f4d7a586da3bb079725e4627c5f9c5d34168dc7e1cab4931b2b402d41ede63e7f036c8ffa6f4136371abb8cf63949d50c8703038181e747e4bda4d92899690c7b0908bce1d535a09b7df386ffb4ae945c2de2e1215d19ad86439210203010001\\\",\\\"sig\\\":\\\"LVjIAnluk2kKdu5tenWBYPW5lTAclaqNW6uVpz2binlT84fdpYQMJEbqRLYQj6/Hy4kOmTiEySsufPN+E8kIG/eaOSnyTjGiUOaEmLaemb1bZnsjjyXUaGic3ErazHNsLx8fvWjdQ4g3QHWjS/hYTFZCYkJK2eiDXdzTi1D+4FxPbm5H4/CAJzfDE378tE/Q+k8WHqyZRWCzKmn1K4IU74UUiTpcFbajja3D/pYx5rehdL/zRaWg5jpyNjIbOxMl7HWN6sRPjixLJhs9nMJONHBn353HINKqCqvRJolc8Fe8aZzDK8E/ngQnSNMCJQEsmyxTO39pyjR3WQ2BkzjHEQ\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.21000000000000005\",\"data\":\"{\\\"msg\\\":\\\"334076650\\\",\\\"time\\\":\\\"1568963137\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101009397502fc12897f8d9cfe958077dd2c834ea57447f8d16be52479c7950830ece06c7bcce51b8aa5daa41dd821620d36d7b11209740d2104ac0cc67fbac1dce23a52465fa565c37b285261c62a512c5783fc2efde81d36053a6714e84c59dff6b5d469f6a4663cec425a914d750f1282ec758476d0032a8b9471988884156bed4e890bb9a113a50513adfe9708e88ffdaeb31accaa34f12784f69094cb0042e372338ff72ece00dd7c3c52fdf5f1411da4d79a37651dc025e80a3dbd821264870031c493997d456b5c84bc665a034b2c7c9036854afa09b97538bb1b1d50eeb29b5634e06ae8a28a319699eb58e85d52c37020a420bde0580487a5b377fc758510203010001\\\",\\\"sig\\\":\\\"JLo1Yc2H6SwTJGBND8KDM70933K4QJqRiwLXIXIpqwU2eCkQ7FX4T6ZrtI+3sRgdxXoD5AkdDXmREjH7M0LOZPnGbYtK5tAbvIcSN1tXJGW9jWxwta1Mo9o1eQm30bE0YSTtj1P+eRZrvhdSjr0YwE8iQxRgmfnvpEwpDlkOO8qKo1PY8QzUG9FYKNPrRsKXt/+Bldktr1O1YQxJfv9ud4GcuGynImffrG05Qlt5/QTGyuYWt7c/qRXNfOmWWjmxa3tMwCC7Vud2tQvvex2Xtmcv6C+PiB8kXVBWq8idPzEIxOheUD7iRXvNpUIKZ7dseJ+CsH9JdPJYo0G20MPs/g\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.22000000000000006\",\"data\":\"{\\\"msg\\\":\\\"1171140307\\\",\\\"time\\\":\\\"1568963138\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a028201010087fab144a85ecd20109073f33cf06c493c2026fa4ef522a4b0a0bbfd5029d8e7106df9ebc36d8710d79807b79d153a6c048704f6fd57c651309fdd821a479e267838f40067d05c63f2766500e87c39205586496d93f3ad8ed5244af8c0568602dc46c2468e05cea6d553ee41dcbc71d0ee2eade0fd7e121046b37850e9066225804a8cdc22fc0f8ec0a7eebe88bcc1387f5db97fa7d702eee2936e727e3525da6b3c10d118ec7d841ae0f84d919b8d45922e1cfdea8592fdba089b3fa85f821c37ef34413b4ab15ab5b126fa343c8b9f363418039f3697a7f2b24b2497b664d0c592d06208cada81fb8e229b58a738f220b379a26149344a796eb10c90cfb30f0203010001\\\",\\\"sig\\\":\\\"OmxlCOxtGraT15xW88jY35Z+rgYpHrUo3gVzzM1e5ZqijRhK4H4TeBta+gOCV9uyMaxuNbLiktS/ilp9XmdewUzjcN5hyVT0GH76mbN85lPYbgCUiJUcS1jMRK3YNQQs3C2xt3q4RsSIl+a6J2631/BXQCRMyHGeFP7qLSgPoGXzcvfBcVHrc85jk5ehJSlkLFFgkHUZU9100x2VJlI+PIG2UFVtD5XEElOl6ozTki3EdsFT5pyxksFBKlRN4j+ou9p31q0i1G+9TDCNNxE7TC9VX5J6kVEy1ZkQDyagr5re3Lrel/9NiOtnRZbO1TnGgTdaivxCw6byx5uZhl3wdQ\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.23000000000000007\",\"data\":\"{\\\"msg\\\":\\\"-1220840602\\\",\\\"time\\\":\\\"1568963139\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100893f87bc182a0debe50587a9ce8dd105dcce17711722faba289b2047a04529631d4401a54ba356d0b8ec0a6a38517463e00b75813047682c67dd17330dac1a01c898cf56f4b6a884b8ed9d46499de9f8889cd2907ecae5f8cbc56161b65d0678d4dd2b20206c31e1bde5fa27f6aaf916eb335f5bd1504f8023d0c75974973405331b73ec8e49e8c17c6d755895f47a45a40178abd9ecb9219ab8d8eb41e67189477a2c34b5a0219933725fa555875256af7352bcb89641c1df3df6eba13e7376eec3dcfb8846d92ec797addfc84adca672cb70c25713ec6f7db8d39ecfca9917c8ce561d70851daaed7a08e4857fc7e1a17e76343b650590949683841b48712d0203010001\\\",\\\"sig\\\":\\\"EEZ42JHm+hBr9j62V8iMk6Cy6GvvljOBolGdaqcXZPCbZjd+wA1lLgX+DCcQLVpv0peiJBac9o0fDtTVu07t7g4cWXqAZvpChG14KfAGlV4RMdmriO/l+Jq1Ui+ZLqgj+MHvtae5rETnHqQI9buA7E/tzlNbbaysqxVQXgTeEUIOKsHUaPfm94k3Gw/Aj7eFsQg94gOCWzqzpCsr5iysdVL/NEzs/4SNjvKe2Pse3tGz6zfILRWaUZBKDi+C4skPeYewNphkfWf7KMugZC06sNc3ORYwGGu3OQ7M8MaI+XKhvLTWA4v7UctUXIZp4X2qIpu/wRBG8AKHXuT39ZisEg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.24000000000000007\",\"data\":\"{\\\"msg\\\":\\\"-431558522\\\",\\\"time\\\":\\\"1568963139\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101008424601b4ff28c9975670f079198a99fb491fcd03befd8983898e375f0693bc391916dc4264094f3b08791d17e680725b61239b61cfdf061ef571c13660ae7ee81b9b7243b0073f1d27c5c92f50d3073542174eb45cb497db350e85c621d1d9fcc6322a0103af3446b5a12b0909aa1ba523a9f1715fad6db3b0592fae52b9924413139be2c395fede671487be5a6c1bf06a298bef942df9690d6e80b11e1c2a104fed1b71197a5af85bd36c1808571884d6acc0a054c811d58304caf8e769732029558c1ccd91a15dc5afb601d4c772fbe3b2035484ed3d4bece192ebb3a7d2a8a7cf4442cfe06bf6ca42aafecf83b0f7612db1d2a012cb680ca348891ef0b470203010001\\\",\\\"sig\\\":\\\"VmHnNBpOyIlsCAXTXzZ6+/oQifo+UFDIKMrwpHA4cXpl6kXb5pvOBIGvqg2Xauh0Y3AmCeNXMOrsgWcZf03SMFn7RlViz64IPco7mfNz+vWcgfGSBvhHF2Mnknj032wm1XFlGNUwiUfa9tqckG5ZQQsXF7bPXVbBjYFmASPi1jYchMBOQYo4RhaxXSzq/oI7n85L35mY/Qg5rMVVQR+eCVp59kBZ0FDiRyx7cB5hNM00Vc7nIGZsLNFBaQeYc5vUYdZDVE/5m9lt+BTgjhErubVfnqiDX6E7yAeRUyQA9CgWCAyElhq54P3sbPX7Qa7LgMHgT6UqeCIrehIm32pbuw\\\\u003d\\\\u003d\\\"}\"}\n";
    //String temp = "{\"index\":\"1_0.0\",\"data\":\"{\\\"msg\\\":\\\"811656496\\\",\\\"time\\\":\\\"1568963120\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100b1ae952674b90a455ebeb2e99b47a04bd1b7dde3067d22fe44c102d32aa3ad80c2e430d9f4729b9adf51278b33a0de0975f8ffc894bd307cfe4527119983d0c713eefbeb64af26b83644b54898107efa1c9932bc8f28197bc5c4d491887e6ac17ba3e9eff60ce0602cd7c16921166ff9f269d44270563098441ad486ce669e3090938d8f4e87124475cd54dc33c60d57bd98b51250bca0e8c69528046dd15864d6be0b60c088ffcc16dafaffe958b7e3a120149f9311dcee90d4083afd80fc82149731f7c6117b3ee6eed59c48c9f69bc4eb468015e9573f7a4a51b5dcaa1bff75efd53a383ff64cdf0d5d28552fe79fc375f1353a4b2ec7e0181df5779f80550203010001\\\",\\\"sig\\\":\\\"ZkfF+pDxSK3a8RFEt2Q7NQzuhWWkmaWsKoahR8O9Diw/GV796kzRh6ZgrViijum4STsWVl9s35bfW+9ElAdTKH14qqCH5ae0tfcGJokiR93aj27Kv/Cm439WQmWhmg8qAeJrwQSydatMJWQ8xDUHq9OG1cFnV77tZV610g/b0Cqdrmm8sTJGCdlRS8l6Yh8OBFAsExbzZIu5w1+JKlaPoy/8usCThC3g3QqeUYrufFp3FRr1eEJxbVnumcsRtjbYmMgTGGXQyOR4czCDh4OrYx28JhAKfeRnntUWafzcWTuqwt1pthLEZaa8lq8gJqlTyjH0GpFEYUrUejOO8kYW/g\\\\u003d\\\\u003d\\\"}\"}\n";
    //String temp ="{\"index\":\"1_0.15\",\"data\":\"{\\\"msg\\\":\\\"1430351332\\\",\\\"time\\\":\\\"1568963131\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101008a5b32b4e066eb050ea66c6ea70a6cb120c649ddde4c9f4149f28215b07cd46995a7e7a2849e14e784ee298df0ab55af5be80d54e8688389a5f601ed40b329b1302dd7b8743b1711a4010f2c22aaaccb3eb96836b8cc97ffa5ee33df6f7a860bcfced4e8815fd2c712cc0463b0414089282fbc5e1ef0d1f26aaac0762515b89b08666900a84079acd04e56574ba43a598dec8bf11024b252e8b6a9030bd08d2d8ef808a5be640802ae476ec6ac33fc20236e5e2e930f21d834918d7347364cce969534646d06a376b6941209257c7b13349a12ddd178ce31137cac5f913e4d32d76708cf1dec1ab9661ed53ce97e95898d6cab9b30b63a0bc817594505ff17a50203010001\\\",\\\"sig\\\":\\\"NQ4vkvSlETDBaMC5OOxiqTJUtndEe1J2N4RYPFOa6RyGaRZtdjg77/v1jefBjsKhPPfkWaP8WQj6hYYGyQqBolx7F0DvU4vCm+puFAEH8nuhH79eIxPFhLPZwH0Oq410hrEvNf98hBMG5uLxUMz1GYRua6I4YkWUxyKnxBFU/TDSkB9CVHxK2CQL+yBWCUSdN1F5P7wDf7zi2lA4uirtGvkL1pQ4JTSfWa/8iCULX4OMkgpF64OzfmtfpgtyogDoTw2JqUMf2mWs7qYelZ1kf7qjtgBz0t/dVJPD2Em9zOb0251GJvIn6JA0g9+fmwQH+PcUv0TCqqmRZZHn7o33ow\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.16\",\"data\":\"{\\\"msg\\\":\\\"491405816\\\",\\\"time\\\":\\\"1568963132\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100adf21b151a346dd059b831ced8466a9fa0c9015e14966e0266ee1ec8a4abbaf17a95d84818bef2d1023070d218955a6b742ee8844a1c68885181c280d9716949ec6d3d2691eaef03c56ca6730f6a1b3bb962d8a7af8cebd92cfdb827acf38ab8c5d71bcededfb0ed53259a1ed72debf3610a1f1c9442cfcf27b5b9a033d65f318ac22c8fa27f348308580fea2127f44667fd94fa655613ef17d90ac2b0d2bca1e25d7f398ea1e89f3718938d53608ca8cf8fc57bc274cacd583a031c35e790146f98f86f97ad0b60c30ef4fe3f51cb87c7ab6fe3105a56a213d4b83f38a4b0779d7b6b34495369123b3b753f99e5e72959e1260ca59b10706fedf66684039f810203010001\\\",\\\"sig\\\":\\\"jmX73KE6e4NHSWdPU2m+ay3+i+tRgD1tEk1XpyWwMmttwLPqg4VVEjzaBMIg45KodCAIQ5LOBtTA9qdNMG/OWwt2BvMrIkvJXLXy0kUb3b0sryru+FVbHvya+rx11YOSVsipOOkvuMpG9sl62A4fyLKgY7yA9hpj/WPXuBSl3Bfx+d+cFFHWZlYUzaDqFS/Q3OxMksoObhZ11YwDtXVDA0xeL3J+AgHcN3myAHpjYQftQko6qxPLPo4atXyAIivdiCmNWCzC+X1zmvitEvO9F+nTcMc4jRifTKbBAzK1bFK5u9gCExRHTGNK2ACwF1yYPDnQU5LwrwcEr7icybMnJg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.17\",\"data\":\"{\\\"msg\\\":\\\"1516180528\\\",\\\"time\\\":\\\"1568963133\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100c560c99453bb00cb2699544ac51a4ccbfc2c07241f7dbea5c579a4114a1994ab70326cd2ea1bf143f3792c78bcfa5f46a5f8fef51f2a5526472dec65161604cc4c78569ed8f328d5affe956f16bb43923e419551e2bd1ffaf8f49594e45f93ab51935b38e728efe4e1398422a74f78f1afbfe92814fd8a4f64331b114b647e0fed47a5e50ea7dc5a1e67802873c1fd34c27e6662d926e74031d2c9b0fa593454acd9e4b6b3061f7f30cea845c78800a94dedbf673e94a0ffa74da8c770f8d7be198f6ac1638aeaa70528c067168c9903dd14631eccfba995ceccc0fc6910811476cec0319ebbb6c967e0f7a6d835755490739ced4b5e256e87b4e7cc76fa7e770203010001\\\",\\\"sig\\\":\\\"RXX2XsGIe9nR0cpU+lXXx2wjQ0/mMTHoIdgDD2+WdFh8qqL3iLu8na74bKi7NbSdrg8M3KlDUBL86GrZ6gysZyMAyn4/oVSl7S9kiAUBdbA8uiTRFHPnZbpiPMiAaR4dV43VwKp/s+bjZ6JqjAa0adWokw0wv5g7b30zw+Xe+SywWoQdg3eQrR989V60P9Djf3/Vfpaw36RblY5tN7Q7uepmxH3kr8Nr7uGP8XXHPYdC/C3XxntLSmi2uf68Y/Zv/ZPO7DhoSev5HJSdQoWWEi/o2/gbOifVXYd1oNwwmxrRkJcl5Dho9zDt/U5IIHu/ctA/Aw+2B5d+wqralBwXMw\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.18000000000000002\",\"data\":\"{\\\"msg\\\":\\\"-1455747013\\\",\\\"time\\\":\\\"1568963134\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100c467d85d9e641ee570a7c9d6c426cd9eeee4e753086e8309e8579869816269f747573d9a539fb49d248d883c84e1c72269cc831ecf684f3ee34dfa4c5472b42fae0fa76e1f0b22ff5b6a5f5695811600bb069d566759e52e049ff0fffd1fa94db509514833a4c94d226625f6b2e2d086b8237f6872345cbc6c30bd2550ffc071a0acd2efed3c94fe549a151e2f76270c7dfbbaa3a78eb53cd58ac5c4a33120ccad1472f414184503a4aebf2f14938018cf569881bf8949ddd83632262f5db4b20f14c7416b06c5075bf8d185cad1c73c2f4414d081680541bf812abde388e8d185fca7c0a221642df34e718ba7e2c01634893b30db02db9e1888bf1cd25bc63f0203010001\\\",\\\"sig\\\":\\\"Xmoaw6ELYN4QYzRbnVx+B9em/P2MmIVjuXANE22XsqIbHX5v9CtXLnC9ctnqnuTQl+PaMPQ79RSLl2RMdC+OD/TqjJs/SYipo2V2jgVq2S+Cy5OYJdobcvu/yIkscuT0CKBc6L0ZTNsyC+lvpVDLwjSBAg4lRog8HErdIkxvToqqbyVbCvPdDggT2wRtKe0WE6eNUFQI95RrzBVaWVYXYgkL0DGOUm6mVkpovhMKpSeWNnB6dknhpe6OdVE+yRE4x5SE1Bt3XhBT/TqzdCY1T2kCjhHh+/n5meLD5h/koGJD6aqD+Fyk3Dakqgv3JghjGYlrL0+uxUOnqycHDfkCOA\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.19000000000000003\",\"data\":\"{\\\"msg\\\":\\\"-1038346812\\\",\\\"time\\\":\\\"1568963135\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100b682d6a6fc8e38bcd80294b8a40689c4b01638b086fddfa3ff250784aae36433c018c7b9660be6a63c80f975db23406e421c8f09db3cda54c6cd818aad481f33428e993a92a65806a735c02b788bac08777fa2e57b16776462f659895bed3211945783bba72efc00fa818721ef9397aca0085c0d530d643312ef9be0b22f9443e3b2d27fd0add5a6a9fc4d00cfa3edb73e7b8fa4a27ced356d14223bf0ddf0d0eb16815e25350a76e73f1fb159534d53073014037de2aaea5119b86676717215f17c1e3d5e18028e77dab818282aec690f97db524df35053aa5daa4f712ecd2867e536cd32b133185dbc3308944572120bd23af30821f5467c36d7f49805c4b30203010001\\\",\\\"sig\\\":\\\"FJhCNoGvoNnBqPi6HZ36UqocuSy5nkW6VM2S9KDmyFo504CwRBNdLSpMLaSMj3jleS+9GEqLF9O8aW8YjxirZaNLz8IJTn76BKGC/oev/OrBVIE+2FoBbIGc4fnFgezlq4TbysEdW+vZ/+6SaLkkBr5gHKmXdGjHXo5+0t7NgU4FPpNEA47o5r1CjxI76Wc04H7uYbTAnPx8bCbK8DzZ7jZwVpEluRwykW9THsfrfW6NpY2MmQZCSXminv4TeMWMfTHmrk+66/0xQXPtZXi4qmsbmY1ZlzFEFJN0WsY+Z3JCXzA4iJpGQENw62RdWhAR0pTkHAHIt9EeKD8QvYTVyg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.20000000000000004\",\"data\":\"{\\\"msg\\\":\\\"1533248531\\\",\\\"time\\\":\\\"1568963136\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100946355613640c70344bde78f9446319f6d081cb5eee13b697b3705a887c98a6a29d0c57096ab880fe9946a1c60fc4ba5933b1441dded4fb626f6d7971eaf62891ddee208ff4376f2cf63494e916eacf3973420c508ba2736f58042d7057935f856fccc1c87aee0baa136a7c5e11e2daa31225929116690649713b17b71fe77a30216f32b6db1e826d6d4f059821fa961517a98e5b550a410972f284571917c9edd2e06d315f4d7a586da3bb079725e4627c5f9c5d34168dc7e1cab4931b2b402d41ede63e7f036c8ffa6f4136371abb8cf63949d50c8703038181e747e4bda4d92899690c7b0908bce1d535a09b7df386ffb4ae945c2de2e1215d19ad86439210203010001\\\",\\\"sig\\\":\\\"LVjIAnluk2kKdu5tenWBYPW5lTAclaqNW6uVpz2binlT84fdpYQMJEbqRLYQj6/Hy4kOmTiEySsufPN+E8kIG/eaOSnyTjGiUOaEmLaemb1bZnsjjyXUaGic3ErazHNsLx8fvWjdQ4g3QHWjS/hYTFZCYkJK2eiDXdzTi1D+4FxPbm5H4/CAJzfDE378tE/Q+k8WHqyZRWCzKmn1K4IU74UUiTpcFbajja3D/pYx5rehdL/zRaWg5jpyNjIbOxMl7HWN6sRPjixLJhs9nMJONHBn353HINKqCqvRJolc8Fe8aZzDK8E/ngQnSNMCJQEsmyxTO39pyjR3WQ2BkzjHEQ\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.21000000000000005\",\"data\":\"{\\\"msg\\\":\\\"334076650\\\",\\\"time\\\":\\\"1568963137\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101009397502fc12897f8d9cfe958077dd2c834ea57447f8d16be52479c7950830ece06c7bcce51b8aa5daa41dd821620d36d7b11209740d2104ac0cc67fbac1dce23a52465fa565c37b285261c62a512c5783fc2efde81d36053a6714e84c59dff6b5d469f6a4663cec425a914d750f1282ec758476d0032a8b9471988884156bed4e890bb9a113a50513adfe9708e88ffdaeb31accaa34f12784f69094cb0042e372338ff72ece00dd7c3c52fdf5f1411da4d79a37651dc025e80a3dbd821264870031c493997d456b5c84bc665a034b2c7c9036854afa09b97538bb1b1d50eeb29b5634e06ae8a28a319699eb58e85d52c37020a420bde0580487a5b377fc758510203010001\\\",\\\"sig\\\":\\\"JLo1Yc2H6SwTJGBND8KDM70933K4QJqRiwLXIXIpqwU2eCkQ7FX4T6ZrtI+3sRgdxXoD5AkdDXmREjH7M0LOZPnGbYtK5tAbvIcSN1tXJGW9jWxwta1Mo9o1eQm30bE0YSTtj1P+eRZrvhdSjr0YwE8iQxRgmfnvpEwpDlkOO8qKo1PY8QzUG9FYKNPrRsKXt/+Bldktr1O1YQxJfv9ud4GcuGynImffrG05Qlt5/QTGyuYWt7c/qRXNfOmWWjmxa3tMwCC7Vud2tQvvex2Xtmcv6C+PiB8kXVBWq8idPzEIxOheUD7iRXvNpUIKZ7dseJ+CsH9JdPJYo0G20MPs/g\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.22000000000000006\",\"data\":\"{\\\"msg\\\":\\\"1171140307\\\",\\\"time\\\":\\\"1568963138\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a028201010087fab144a85ecd20109073f33cf06c493c2026fa4ef522a4b0a0bbfd5029d8e7106df9ebc36d8710d79807b79d153a6c048704f6fd57c651309fdd821a479e267838f40067d05c63f2766500e87c39205586496d93f3ad8ed5244af8c0568602dc46c2468e05cea6d553ee41dcbc71d0ee2eade0fd7e121046b37850e9066225804a8cdc22fc0f8ec0a7eebe88bcc1387f5db97fa7d702eee2936e727e3525da6b3c10d118ec7d841ae0f84d919b8d45922e1cfdea8592fdba089b3fa85f821c37ef34413b4ab15ab5b126fa343c8b9f363418039f3697a7f2b24b2497b664d0c592d06208cada81fb8e229b58a738f220b379a26149344a796eb10c90cfb30f0203010001\\\",\\\"sig\\\":\\\"OmxlCOxtGraT15xW88jY35Z+rgYpHrUo3gVzzM1e5ZqijRhK4H4TeBta+gOCV9uyMaxuNbLiktS/ilp9XmdewUzjcN5hyVT0GH76mbN85lPYbgCUiJUcS1jMRK3YNQQs3C2xt3q4RsSIl+a6J2631/BXQCRMyHGeFP7qLSgPoGXzcvfBcVHrc85jk5ehJSlkLFFgkHUZU9100x2VJlI+PIG2UFVtD5XEElOl6ozTki3EdsFT5pyxksFBKlRN4j+ou9p31q0i1G+9TDCNNxE7TC9VX5J6kVEy1ZkQDyagr5re3Lrel/9NiOtnRZbO1TnGgTdaivxCw6byx5uZhl3wdQ\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.23000000000000007\",\"data\":\"{\\\"msg\\\":\\\"-1220840602\\\",\\\"time\\\":\\\"1568963139\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a0282010100893f87bc182a0debe50587a9ce8dd105dcce17711722faba289b2047a04529631d4401a54ba356d0b8ec0a6a38517463e00b75813047682c67dd17330dac1a01c898cf56f4b6a884b8ed9d46499de9f8889cd2907ecae5f8cbc56161b65d0678d4dd2b20206c31e1bde5fa27f6aaf916eb335f5bd1504f8023d0c75974973405331b73ec8e49e8c17c6d755895f47a45a40178abd9ecb9219ab8d8eb41e67189477a2c34b5a0219933725fa555875256af7352bcb89641c1df3df6eba13e7376eec3dcfb8846d92ec797addfc84adca672cb70c25713ec6f7db8d39ecfca9917c8ce561d70851daaed7a08e4857fc7e1a17e76343b650590949683841b48712d0203010001\\\",\\\"sig\\\":\\\"EEZ42JHm+hBr9j62V8iMk6Cy6GvvljOBolGdaqcXZPCbZjd+wA1lLgX+DCcQLVpv0peiJBac9o0fDtTVu07t7g4cWXqAZvpChG14KfAGlV4RMdmriO/l+Jq1Ui+ZLqgj+MHvtae5rETnHqQI9buA7E/tzlNbbaysqxVQXgTeEUIOKsHUaPfm94k3Gw/Aj7eFsQg94gOCWzqzpCsr5iysdVL/NEzs/4SNjvKe2Pse3tGz6zfILRWaUZBKDi+C4skPeYewNphkfWf7KMugZC06sNc3ORYwGGu3OQ7M8MaI+XKhvLTWA4v7UctUXIZp4X2qIpu/wRBG8AKHXuT39ZisEg\\\\u003d\\\\u003d\\\"}\"}, {\"index\":\"1_0.24000000000000007\",\"data\":\"{\\\"msg\\\":\\\"-431558522\\\",\\\"time\\\":\\\"1568963139\\\",\\\"pubKey\\\":\\\"30820122300d06092a864886f70d01010105000382010f003082010a02820101008424601b4ff28c9975670f079198a99fb491fcd03befd8983898e375f0693bc391916dc4264094f3b08791d17e680725b61239b61cfdf061ef571c13660ae7ee81b9b7243b0073f1d27c5c92f50d3073542174eb45cb497db350e85c621d1d9fcc6322a0103af3446b5a12b0909aa1ba523a9f1715fad6db3b0592fae52b9924413139be2c395fede671487be5a6c1bf06a298bef942df9690d6e80b11e1c2a104fed1b71197a5af85bd36c1808571884d6acc0a054c811d58304caf8e769732029558c1ccd91a15dc5afb601d4c772fbe3b2035484ed3d4bece192ebb3a7d2a8a7cf4442cfe06bf6ca42aafecf83b0f7612db1d2a012cb680ca348891ef0b470203010001\\\",\\\"sig\\\":\\\"VmHnNBpOyIlsCAXTXzZ6+/oQifo+UFDIKMrwpHA4cXpl6kXb5pvOBIGvqg2Xauh0Y3AmCeNXMOrsgWcZf03SMFn7RlViz64IPco7mfNz+vWcgfGSBvhHF2Mnknj032wm1XFlGNUwiUfa9tqckG5ZQQsXF7bPXVbBjYFmASPi1jYchMBOQYo4RhaxXSzq/oI7n85L35mY/Qg5rMVVQR+eCVp59kBZ0FDiRyx7cB5hNM00Vc7nIGZsLNFBaQeYc5vUYdZDVE/5m9lt+BTgjhErubVfnqiDX6E7yAeRUyQA9CgWCAyElhq54P3sbPX7Qa7LgMHgT6UqeCIrehIm32pbuw\\\\u003d\\\\u003d\\\"}\"}\n";

        String[] dataSplit = temp.split("&&");
        String nodenumber = dataSplit[1];
        String txData = dataSplit[2];
        HashMap<String, Integer> tempHash = new HashMap<>();
        String stringTxData = txData.replaceAll("[\\[\\]]", "");
        String[] splitTxData = stringTxData.split(", ");
        List<String> tempList = new ArrayList<>();
        List<String> tempList2 = new ArrayList<>();
        int min = 0;
        int max = 0;

    for (int i=0; i<splitTxData.length; i++){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(splitTxData[i]);
        String data = element.getAsJsonObject().get("data").getAsString();
        String index = element.getAsJsonObject().get("index").getAsString();
        tempList.add(index);
        tempList2.add(data);

        tempHash.put(nodenumber,tempList.size());
        tempHash.put("2", 2);
        tempHash.put("4", 8);

        Collection values = tempHash.values();
        min = (int) Collections.min(values);
        max = (int) Collections.max(values);
    }
    if (min != max){
        List<String> tempList3 = tempList2.subList(0,min);
        int hash = tempList3.hashCode();
    }

    if (min == max){

    }

    //Todo 대충 이런느낌으로 돌려가면서 테스트 해봐야할듯..





    }
}
