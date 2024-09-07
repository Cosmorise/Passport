package me.combimagnetron.passport.internal.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface Item {



    class Impl<M> {
        private final static Impl<Integer> EMPTY = Impl.item(/*9648*/ 26842);
        private List<Component> lore = new ArrayList<>();
        private MutableNBTCompound nbtCompound;
        private MutableNBTCompound tag;
        private int amount;
        private final M material;
        private int customModelData;
        private Component name;

        private Impl(M material, int amount) {
            this.material = material;
            this.amount = amount;
            this.nbtCompound = NBTCompound.EMPTY.toMutableCompound();
            //this.nbtCompound.set("id", NBT.Int((int) material));
            this.nbtCompound.set("count", NBT.Byte(amount));
            this.tag = nbtCompound.set("tag", NBTCompound.EMPTY);
            this.tag.set("display", NBTCompound.EMPTY);
        }

        public static <T> Impl<T> item(T material) {
            return new Impl<>(material, 1);
        }

        public static <T> Impl<T> item(T material, int amount) {
            return new Impl<>(material, amount);
        }

        public static Impl<?> empty() {
            return EMPTY;
        }

        public Impl<M> customModelData(int customModelData) {
            this.customModelData = customModelData;
            this.nbtCompound.set("CustomModelData", NBT.Int(this.customModelData));
            return this;
        }

        public Impl<M> name(Component name) {
            this.name = name;
            this.nbtCompound.getCompound("display")
                    .modify(builder -> builder.set("Name", NBT.String(GsonComponentSerializer.gson().serialize(this.name))));
            return this;
        }

        public Impl<M> lore(Component... lore) {
            this.lore.addAll(List.of(lore));
            this.nbtCompound.getCompound("display")
                    .modify(builder -> {
                        builder.set("Lore", NBT.List(
                                NBTType.TAG_String, this.lore.stream().map(component -> NBT.String(GsonComponentSerializer.gson().serialize(component))).toList()
                        ));
                    });
            return this;
        }

        public M material() {
            return this.material;
        }

        public int customModelData() {
            return this.customModelData;
        }

        public Component name() {
            return this.name;
        }

        public int amount() {
            return this.amount;
        }

        public Collection<Component> lore() {
            return this.lore;
        }

        public NBTCompound nbt() {
            return nbtCompound.toCompound();
        }

        public record Slot(Impl<?> item, int slot) {

        }

        public interface NbtAdapter<T> {
            NbtAdapter<Integer> INT = AdapterImpl.of(nbt -> (int) nbt.getValue(), NBT::Int);
            NbtAdapter<String> STRING = AdapterImpl.of(nbt -> (String) nbt.getValue(), NBT::String);

            NBT nbt(T object);

            T object(NBT nbt);

            class AdapterImpl<T> implements NbtAdapter<T> {
                private final Function<NBT, T> objectFunction;
                private final Function<T, NBT> nbtFunction;

                public static <V> NbtAdapter<V> of(Function<NBT, V> objectFunction, Function<V, NBT> nbtFunction) {
                    return new AdapterImpl<>(objectFunction, nbtFunction);
                }

                private AdapterImpl(Function<NBT, T> objectFunction, Function<T, NBT> nbtFunction) {
                    this.nbtFunction = nbtFunction;
                    this.objectFunction = objectFunction;
                }

                @Override
                public NBT nbt(T object) {
                    return nbtFunction.apply(object);
                }

                @Override
                public T object(NBT nbt) {
                    return objectFunction.apply(nbt);
                }
            }

        }


    }

}
