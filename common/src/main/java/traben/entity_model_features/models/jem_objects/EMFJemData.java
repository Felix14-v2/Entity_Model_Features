package traben.entity_model_features.models.jem_objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFOptiFineMappings2;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;

public class EMFJemData {
    public final LinkedHashMap<String, String> finalAnimationsForModel = new LinkedHashMap<>();
    private final String REGEX_PREFIX = "(?<=([^a-zA-Z0-9_]|^))";
    private final String REGEX_SUFFIX = "(?=([^a-zA-Z0-9_]|$))";
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();
    public LinkedList<EMFPartData> originalModelsForReadingOnly;
    public String fileName = "none";
    public String mobName = "none";
    public Identifier customTexture = null;

    public void sendFileName(String fileName) {
        this.fileName = fileName;
        this.mobName = fileName.replace("optifine/cem/", "").replace(".jem", "");
    }

    public void prepare() {
        originalModelsForReadingOnly = new LinkedList<>(models);

        if (!texture.isBlank()) {
            if (!this.texture.contains(".png")) this.texture = this.texture + ".png";
            //if no folder parenting assume it is relative to model
            if (!this.texture.contains("/")) this.texture = "optifine/cem/" + this.texture;
            Identifier possibleTexture = new Identifier(texture);
            if (MinecraftClient.getInstance().getResourceManager().getResource(possibleTexture).isPresent()) {
                customTexture = possibleTexture;
            }
        }


        String mobNameMinusVariant = mobName.replaceAll("(?<=\\w)[0-9]", "");
        //vanilla parenting adjustments
        Map<String, EMFOptiFineMappings2.PartAndChildName> map = EMFOptiFineMappings2.getMapOf(mobNameMinusVariant);
        Set<String> foundChildren = new HashSet<>();


        //change all part values to their vanilla counterparts
        for (EMFPartData partData :
                models) {
            if (partData.part != null) {
                if (map.containsKey(partData.part)) {
                    String newPartName = map.get(partData.part).partName();
                    if (partData.id.equals(partData.part)) {//|| partData.id.isBlank()
                        partData.id = newPartName;
                    }
                    partData.part = newPartName;

                }
            }
        }

        //add any missing parts as blank before children removal checks
        LinkedList<EMFPartData> missingModels = new LinkedList<EMFPartData>();
        for (EMFOptiFineMappings2.PartAndChildName data :
                map.values()) {
            String name = data.partName();
            boolean found = false;
            for (EMFPartData partData :
                    models) {
                if (name.equals(partData.part) && (!partData.attach|| partData.id.equals(partData.part))){//dont count attached parts for now
                    found = true;
                    break;
                }
            }
            if (!found) missingModels.add(EMFPartData.getBlankPartWithIDOf(name));
        }
        if (missingModels.size() > 0) {
            EMFUtils.EMFModError("These parts were missing from [" + fileName + "]: " + missingModels);
            models.addAll(missingModels);
        }

        //attach logic
        LinkedList<EMFPartData> modelsAttach = new LinkedList<>();
        Iterator<EMFPartData> modelsIterator = models.iterator();
        while (modelsIterator.hasNext()) {
            EMFPartData model = modelsIterator.next();
            if (model.attach && !model.id.equals(model.part)) {
                modelsAttach.add(model);
                modelsIterator.remove();
            }
        }
        for (EMFPartData model :
                modelsAttach) {
            if (model.part != null) {
                for (EMFPartData partData :
                        models) {
                    if (partData.part.equals(model.part)) {
                        partData.submodels.add(model);//todo check if needs to be merge or just child add :/
                        model.part = null;
                    }
                }
            } else {
                //pls no
            }
        }




        //copy all children into their parents lists
        for (Map.Entry<String, EMFOptiFineMappings2.PartAndChildName> entry :
                map.entrySet()) {

            if (entry.getValue().childNamesToExpect().size() > 0) {
                //found entry with child
                EMFPartData parent = getFirstPartInModelsIgnoreAttach(entry.getValue().partName());
                if (parent != null) {
                    for (String childName :
                            entry.getValue().childNamesToExpect()) {
                        if (childName.startsWith("!")) {//map marker to put an empty child and not to move this child because OPTIFINE FUCKED UP FROGS
                            parent.submodels.add(EMFPartData.getBlankPartWithIDOf(childName.replaceFirst("!", "")));
                        } else {
                            EMFPartData child = getFirstPartInModelsIgnoreAttach(childName);
                            if (child != null) {
                                parent.submodels.add(child);
                            } else {
                                //oof no child, this can happen with the shitty things in vanilla like wolves real_tail :/
                                parent.submodels.add(EMFPartData.getBlankPartWithIDOf(childName));
                            }
                            foundChildren.add(childName);
                        }
                    }
                }
            }
        }


        //all children have been added to their parents time to remove children from the top level list
        models.removeIf(topLevelPart -> foundChildren.contains(topLevelPart.part));


        //now all parts follow exactly the vanilla model root parent structure
        //attaches have also been applied currently only as children

        for (EMFPartData model :
                models) {
            model.prepare(0, textureSize, texture, new float[]{0, 0, 0});
        }


        ///prep animations
        SortedMap<String, EMFPartData> alphabeticalOrderedParts = new TreeMap<>(Comparator.naturalOrder());
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("originalModelsForReadingOnly #= " + originalModelsForReadingOnly.size());
        for (EMFPartData partData :
                originalModelsForReadingOnly) {
            alphabeticalOrderedParts.put(partData.id, partData);
        }

        LinkedList<LinkedHashMap<String, String>> allTopLevelPropertiesOrdered = new LinkedList<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("alphabeticalOrderedParts = " + alphabeticalOrderedParts);
        for (EMFPartData part :
                alphabeticalOrderedParts.values()) {
            if (part.animations != null && part.animations.length != 0) {
                //todo replace 'this' and parenting to represent actual model part
                allTopLevelPropertiesOrdered.addAll(Arrays.asList(part.animations));
            }
        }
        LinkedHashMap<String, String> combinedPropertiesOrdered = new LinkedHashMap<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("allTopLevelPropertiesOrdered = " + allTopLevelPropertiesOrdered);
        for (LinkedHashMap<String, String> properties :
                allTopLevelPropertiesOrdered) {
            if (!properties.isEmpty()) {
                combinedPropertiesOrdered.putAll(properties);
            }
        }
        //LinkedHashMap<String,String>  finalNameFilteredPropertiesOrdered = new LinkedHashMap<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("combinedPropertiesOrdered = " + combinedPropertiesOrdered);
        for (Map.Entry<String, String> entry :
                combinedPropertiesOrdered.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                String animationKey = entry.getKey().replaceAll("\\s", "");
                String animationExpression = entry.getValue().replaceAll("\\s", "");


                //there is no way out of this we have to loop each mapping for each entry to cover all possible part pointers
                //todo can likely optimize further
                if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage("map = " + map);
                for (Map.Entry<String, EMFOptiFineMappings2.PartAndChildName> optifineMapEntry :
                        map.entrySet()) {
                    String optifinePartName = optifineMapEntry.getKey();
                    String vanillaPartName = optifineMapEntry.getValue().partName();
                    if (!optifinePartName.equals(vanillaPartName)) {//skip if no change needed
                        if (animationKey.contains(optifinePartName)) {//this is faster than the lookbehind and ahead regex it will save us time if the string does not contain a part reference
                            animationKey = animationKey.replaceAll(
                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly but the look ahead and behind are essential
                        }
                        if (animationExpression.contains(optifinePartName)) {
                            animationExpression = animationExpression.replaceAll(
                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly
                        }
                    }
                }
                //expression and key now have vanilla part names and references as well as no spaces
                finalAnimationsForModel.put(animationKey, animationExpression);
            } else {
                System.out.println("null key 1346341");
            }
            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage("finalAnimationsForModel =" + finalAnimationsForModel);
        }
        ///finished animations preprocess
    }

    private EMFPartData getFirstPartInModelsIgnoreAttach(String partName) {
        //return override part or first of the attaching parts if no override found
        EMFPartData first = null;
        for (EMFPartData emfPartData :
                models) {
            if (emfPartData.part.equals(partName)) {// && (!emfPartData.attach || emfPartData.part.equals(emfPartData.id)))
                if (!emfPartData.attach) {
                    return emfPartData;
                } else if (first == null) {
                    first = emfPartData;
                }
            }
        }
        return first;
    }


    @Override
    public String toString() {
        return "EMF_JemData{" +
                "texture='" + texture + '\'' +
                ", textureSize=" + Arrays.toString(textureSize) +
                ", shadow_size=" + shadow_size +
                ", models=" + models.toString() +
                '}';
    }

}
